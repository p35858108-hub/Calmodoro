package com.example.timer

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.SystemClock
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.example.audio.SoundManager
import com.example.data.local.AppDatabase
import com.example.data.local.entity.PomodoroSessionEntity
import com.example.receiver.AlarmReceiver
import com.example.service.PomodoroService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import kotlin.math.ceil

class PomodoroEngine private constructor(private val context: Context) {

    private val engineScope = CoroutineScope(Dispatchers.Main + Job())
    private var tickerJob: Job? = null

    private val _stateFlow = MutableStateFlow(PomodoroState())
    val stateFlow: StateFlow<PomodoroState> = _stateFlow.asStateFlow()

    private var startRealtime: Long = 0L
    private var alreadyElapsedMs: Long = 0L
    private var targetDurationMs: Long = 25 * 60 * 1000L

    init {
        // Load initial settings
        engineScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context, engineScope)
            val settings = db.userSettingsDao().getSettingsSync()
            if (settings != null) {
                _stateFlow.update { current ->
                    current.copy(
                        remainingSeconds = settings.focusDurationMinutes * 60L,
                        totalDurationSeconds = settings.focusDurationMinutes * 60L,
                        soundChoice = settings.soundChoice,
                        notificationsEnabled = settings.notificationsEnabled,
                        vibrationEnabled = settings.vibrationEnabled
                    )
                }
                targetDurationMs = settings.focusDurationMinutes * 60 * 1000L
            }
        }
    }

    fun setMode(mode: PomodoroMode, durationMinutes: Int, taskId: Long? = null, taskTitle: String = "") {
        if (_stateFlow.value.isRunning) {
            pause()
        }
        val durationSec = durationMinutes * 60L
        targetDurationMs = durationSec * 1000L
        alreadyElapsedMs = 0L
        startRealtime = 0L

        _stateFlow.update { current ->
            current.copy(
                mode = mode,
                isRunning = false,
                isPaused = false,
                remainingSeconds = durationSec,
                totalDurationSeconds = durationSec,
                activeTaskId = taskId,
                activeTaskTitle = taskTitle
            )
        }
    }

    fun startOrResume() {
        val currentState = _stateFlow.value
        if (currentState.isRunning) return

        startRealtime = SystemClock.elapsedRealtime()
        _stateFlow.update { it.copy(isRunning = true, isPaused = false) }

        // Play Start Sound
        try {
            SoundManager.getInstance(context).playStart()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Haptic Feedback
        triggerHaptic(50)

        // Schedule Exact Alarm
        val remainingMs = maxOf(0L, targetDurationMs - alreadyElapsedMs)
        scheduleExactAlarm(remainingMs)

        // Start Foreground Service
        if (currentState.notificationsEnabled) {
            try {
                PomodoroService.startService(context)
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        // Start Ticker Loop
        startTicker()
    }

    fun pause() {
        val currentState = _stateFlow.value
        if (!currentState.isRunning) return

        tickerJob?.cancel()
        val elapsedSinceStart = SystemClock.elapsedRealtime() - startRealtime
        alreadyElapsedMs += maxOf(0L, elapsedSinceStart)
        startRealtime = 0L

        val remainingMs = maxOf(0L, targetDurationMs - alreadyElapsedMs)
        val remainingSec = ceil(remainingMs / 1000.0).toLong()

        _stateFlow.update {
            it.copy(
                isRunning = false,
                isPaused = true,
                remainingSeconds = remainingSec
            )
        }

        // Cancel pending alarm while paused
        cancelExactAlarm()

        // Play Pause Sound
        try {
            SoundManager.getInstance(context).playPause()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        triggerHaptic(40)
    }

    fun resume() {
        startOrResume()
    }

    fun reset() {
        tickerJob?.cancel()
        cancelExactAlarm()
        alreadyElapsedMs = 0L
        startRealtime = 0L

        val totalSec = _stateFlow.value.totalDurationSeconds
        targetDurationMs = totalSec * 1000L

        _stateFlow.update {
            it.copy(
                isRunning = false,
                isPaused = false,
                remainingSeconds = totalSec
            )
        }

        try {
            SoundManager.getInstance(context).playClick()
        } catch (e: Exception) {
            e.printStackTrace()
        }

        try {
            PomodoroService.stopService(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun startTicker() {
        tickerJob?.cancel()
        tickerJob = engineScope.launch {
            while (_stateFlow.value.isRunning) {
                delay(200)
                val currentRealtime = SystemClock.elapsedRealtime()
                val elapsedSinceStart = currentRealtime - startRealtime
                val totalElapsed = alreadyElapsedMs + elapsedSinceStart
                val remainingMs = maxOf(0L, targetDurationMs - totalElapsed)
                val remainingSec = ceil(remainingMs / 1000.0).toLong()

                if (remainingMs <= 0) {
                    _stateFlow.update { it.copy(remainingSeconds = 0) }
                    finishSession()
                    break
                } else {
                    _stateFlow.update { it.copy(remainingSeconds = remainingSec) }
                }
            }
        }
    }

    private fun finishSession() {
        tickerJob?.cancel()
        cancelExactAlarm()

        val currentState = _stateFlow.value
        val soundChoice = currentState.soundChoice

        // Play finish alarm sound
        try {
            SoundManager.getInstance(context).playFinish(soundChoice)
        } catch (e: Exception) {
            e.printStackTrace()
        }

        // Trigger finish vibration
        triggerFinishHaptic()

        // Record session to Room
        engineScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context, engineScope)
            db.pomodoroSessionDao().insertSession(
                PomodoroSessionEntity(
                    taskId = currentState.activeTaskId,
                    taskTitle = currentState.activeTaskTitle.ifEmpty { currentState.mode.label },
                    mode = currentState.mode.name,
                    durationSeconds = currentState.totalDurationSeconds.toInt(),
                    completedAt = System.currentTimeMillis(),
                    wasCompleted = true
                )
            )
        }

        _stateFlow.update {
            it.copy(
                isRunning = false,
                isPaused = false,
                remainingSeconds = it.totalDurationSeconds
            )
        }

        try {
            PomodoroService.stopService(context)
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    fun addMinutes(deltaMinutes: Int) {
        val deltaMs = deltaMinutes * 60 * 1000L
        targetDurationMs = maxOf(60 * 1000L, targetDurationMs + deltaMs)
        val newTotalSec = maxOf(60L, _stateFlow.value.totalDurationSeconds + deltaMinutes * 60L)

        val remainingMs = maxOf(0L, targetDurationMs - (alreadyElapsedMs + if (startRealtime > 0) (SystemClock.elapsedRealtime() - startRealtime) else 0L))
        val remainingSec = ceil(remainingMs / 1000.0).toLong()

        _stateFlow.update {
            it.copy(
                remainingSeconds = remainingSec,
                totalDurationSeconds = newTotalSec
            )
        }

        if (_stateFlow.value.isRunning) {
            cancelExactAlarm()
            scheduleExactAlarm(remainingMs)
        }
        triggerHaptic(20)
    }

    fun skipMode() {
        val nextMode = when (_stateFlow.value.mode) {
            PomodoroMode.FOCUS -> PomodoroMode.SHORT_BREAK
            PomodoroMode.SHORT_BREAK -> PomodoroMode.FOCUS
            PomodoroMode.LONG_BREAK -> PomodoroMode.FOCUS
        }
        engineScope.launch(Dispatchers.IO) {
            val db = AppDatabase.getDatabase(context, engineScope)
            val settings = db.userSettingsDao().getSettingsSync()
            val durationMin = when (nextMode) {
                PomodoroMode.FOCUS -> settings?.focusDurationMinutes ?: 25
                PomodoroMode.SHORT_BREAK -> settings?.shortBreakMinutes ?: 5
                PomodoroMode.LONG_BREAK -> settings?.longBreakMinutes ?: 15
            }
            engineScope.launch(Dispatchers.Main) {
                setMode(
                    mode = nextMode,
                    durationMinutes = durationMin,
                    taskId = if (nextMode == PomodoroMode.FOCUS) _stateFlow.value.activeTaskId else null,
                    taskTitle = if (nextMode == PomodoroMode.FOCUS) _stateFlow.value.activeTaskTitle else ""
                )
            }
        }
    }

    fun onAlarmTriggered() {
        if (_stateFlow.value.isRunning) {
            finishSession()
        }
    }

    fun updateSettings(
        soundChoice: String,
        notifications: Boolean,
        vibration: Boolean,
        focusMinutes: Int,
        shortMinutes: Int,
        longMinutes: Int
    ) {
        _stateFlow.update { current ->
            val newTotal = when (current.mode) {
                PomodoroMode.FOCUS -> focusMinutes * 60L
                PomodoroMode.SHORT_BREAK -> shortMinutes * 60L
                PomodoroMode.LONG_BREAK -> longMinutes * 60L
            }
            if (!current.isRunning && !current.isPaused) {
                targetDurationMs = newTotal * 1000L
                current.copy(
                    soundChoice = soundChoice,
                    notificationsEnabled = notifications,
                    vibrationEnabled = vibration,
                    remainingSeconds = newTotal,
                    totalDurationSeconds = newTotal
                )
            } else {
                current.copy(
                    soundChoice = soundChoice,
                    notificationsEnabled = notifications,
                    vibrationEnabled = vibration
                )
            }
        }
    }

    private fun scheduleExactAlarm(durationMs: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val currentState = _stateFlow.value
        val intent = Intent(context, AlarmReceiver::class.java).apply {
            putExtra(AlarmReceiver.EXTRA_TITLE, "¡${currentState.mode.label} Completado!")
            putExtra(
                AlarmReceiver.EXTRA_MESSAGE,
                if (currentState.activeTaskTitle.isNotEmpty()) {
                    "Has completado tu bloque de: ${currentState.activeTaskTitle}"
                } else {
                    "Tu sesión de ${currentState.mode.label.lowercase()} ha finalizado."
                }
            )
            putExtra(AlarmReceiver.EXTRA_SOUND_CHOICE, currentState.soundChoice)
            putExtra(AlarmReceiver.EXTRA_VIBRATION, currentState.vibrationEnabled)
        }

        val pendingIntent = PendingIntent.getBroadcast(
            context,
            AlarmReceiver.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val triggerAtMillis = System.currentTimeMillis() + durationMs

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                if (alarmManager.canScheduleExactAlarms()) {
                    alarmManager.setExactAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                } else {
                    alarmManager.setAndAllowWhileIdle(
                        AlarmManager.RTC_WAKEUP,
                        triggerAtMillis,
                        pendingIntent
                    )
                }
            } else {
                alarmManager.setExactAndAllowWhileIdle(
                    AlarmManager.RTC_WAKEUP,
                    triggerAtMillis,
                    pendingIntent
                )
            }
        } catch (e: SecurityException) {
            e.printStackTrace()
        }
    }

    private fun cancelExactAlarm() {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AlarmReceiver::class.java)
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            AlarmReceiver.NOTIFICATION_ID,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        alarmManager.cancel(pendingIntent)
    }

    private fun triggerHaptic(durationMs: Long) {
        if (!_stateFlow.value.vibrationEnabled) return
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v?.vibrate(VibrationEffect.createOneShot(durationMs, VibrationEffect.DEFAULT_AMPLITUDE))
                } else {
                    @Suppress("DEPRECATION")
                    v?.vibrate(durationMs)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun triggerFinishHaptic() {
        if (!_stateFlow.value.vibrationEnabled) return
        try {
            val pattern = longArrayOf(0, 400, 200, 400, 200, 500)
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                vm?.defaultVibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                val v = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    v?.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    v?.vibrate(pattern, -1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: PomodoroEngine? = null

        fun getInstance(context: Context): PomodoroEngine {
            return INSTANCE ?: synchronized(this) {
                val instance = PomodoroEngine(context.applicationContext)
                INSTANCE = instance
                instance
            }
        }
    }
}
