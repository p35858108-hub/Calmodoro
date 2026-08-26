package com.example.service

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.IBinder
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.timer.PomodoroEngine
import com.example.timer.PomodoroState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.collectLatest
import kotlinx.coroutines.launch

class PomodoroService : Service() {

    companion object {
        const val CHANNEL_ID = "calmodoro_fgs_channel"
        const val NOTIFICATION_ID = 1001

        const val ACTION_START = "com.example.action.START"
        const val ACTION_PAUSE = "com.example.action.PAUSE"
        const val ACTION_RESUME = "com.example.action.RESUME"
        const val ACTION_RESET = "com.example.action.RESET"
        const val ACTION_STOP = "com.example.action.STOP"

        fun startService(context: Context) {
            val intent = Intent(context, PomodoroService::class.java).apply {
                action = ACTION_START
            }
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                context.startForegroundService(intent)
            } else {
                context.startService(intent)
            }
        }

        fun stopService(context: Context) {
            val intent = Intent(context, PomodoroService::class.java).apply {
                action = ACTION_STOP
            }
            context.startService(intent)
        }
    }

    private val serviceScope = CoroutineScope(Dispatchers.Main + Job())
    private var stateObserverJob: Job? = null

    override fun onCreate() {
        super.onCreate()
        createNotificationChannel()
        startForeground(NOTIFICATION_ID, buildNotification(PomodoroEngine.getInstance(this).stateFlow.value))
        observeEngineState()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        val engine = PomodoroEngine.getInstance(this)
        when (intent?.action) {
            ACTION_PAUSE -> engine.pause()
            ACTION_RESUME -> engine.resume()
            ACTION_RESET -> engine.reset()
            ACTION_STOP -> {
                engine.reset()
                stopForeground(STOP_FOREGROUND_REMOVE)
                stopSelf()
                return START_NOT_STICKY
            }
        }
        return START_STICKY
    }

    private fun observeEngineState() {
        stateObserverJob?.cancel()
        stateObserverJob = serviceScope.launch {
            PomodoroEngine.getInstance(this@PomodoroService).stateFlow.collectLatest { state ->
                if (!state.isRunning && !state.isPaused) {
                    stopForeground(STOP_FOREGROUND_REMOVE)
                    stopSelf()
                } else {
                    val notification = buildNotification(state)
                    val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
                    notificationManager.notify(NOTIFICATION_ID, notification)
                }
            }
        }
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Temporizador Pomodoro Activo",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Muestra el tiempo restante y controles interactivos del Pomodoro"
                setShowBadge(false)
            }
            val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
        }
    }

    private fun buildNotification(state: PomodoroState): Notification {
        val openAppIntent = Intent(this, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_SINGLE_TOP or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val contentPendingIntent = PendingIntent.getActivity(
            this,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val modeTitle = "${state.mode.label} (${state.mode.statusText})"
        val contentSubtitle = if (state.activeTaskTitle.isNotEmpty()) {
            "${state.formattedTime} • ${state.activeTaskTitle}"
        } else {
            "${state.formattedTime} • Calmodoro"
        }

        val builder = NotificationCompat.Builder(this, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(contentSubtitle)
            .setContentText(modeTitle)
            .setSubText(state.mode.label)
            .setContentIntent(contentPendingIntent)
            .setOngoing(state.isRunning)
            .setOnlyAlertOnce(true)
            .setProgress(
                state.totalDurationSeconds.toInt(),
                (state.totalDurationSeconds - state.remainingSeconds).toInt(),
                false
            )
            .setVisibility(NotificationCompat.VISIBILITY_PUBLIC)

        // Actions
        if (state.isRunning) {
            // Pause action
            val pauseIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_PAUSE }
            val pausePendingIntent = PendingIntent.getService(
                this, 1, pauseIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(android.R.drawable.ic_media_pause, "Pausar", pausePendingIntent)
        } else if (state.isPaused) {
            // Resume action
            val resumeIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_RESUME }
            val resumePendingIntent = PendingIntent.getService(
                this, 2, resumeIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
            )
            builder.addAction(android.R.drawable.ic_media_play, "Reanudar", resumePendingIntent)
        }

        // Reset action
        val resetIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_RESET }
        val resetPendingIntent = PendingIntent.getService(
            this, 3, resetIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(android.R.drawable.ic_menu_revert, "Reiniciar", resetPendingIntent)

        // Stop action
        val stopIntent = Intent(this, PomodoroService::class.java).apply { action = ACTION_STOP }
        val stopPendingIntent = PendingIntent.getService(
            this, 4, stopIntent, PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
        builder.addAction(android.R.drawable.ic_menu_close_clear_cancel, "Detener", stopPendingIntent)

        return builder.build()
    }

    override fun onDestroy() {
        super.onDestroy()
        stateObserverJob?.cancel()
    }

    override fun onBind(intent: Intent?): IBinder? = null
}
