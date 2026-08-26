package com.example

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.audio.SoundManager
import com.example.data.local.AppDatabase
import com.example.data.repository.PomodoroRepository
import com.example.data.repository.ScheduleRepository
import com.example.data.repository.SettingsRepository
import com.example.data.repository.TaskRepository
import com.example.receiver.AlarmReceiver
import com.example.service.PomodoroService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob

class CalmodoroApp : Application() {

    val applicationScope = CoroutineScope(SupervisorJob() + Dispatchers.Default)

    val database by lazy { AppDatabase.getDatabase(this, applicationScope) }
    val taskRepository by lazy { TaskRepository(database.taskDao()) }
    val scheduleRepository by lazy { ScheduleRepository(database.weeklyScheduleDao()) }
    val pomodoroRepository by lazy { PomodoroRepository(database.pomodoroSessionDao()) }
    val settingsRepository by lazy { SettingsRepository(database.userSettingsDao()) }

    override fun onCreate() {
        super.onCreate()
        createNotificationChannels()
        // Pre-warm sound pool
        SoundManager.getInstance(this)
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationManager = getSystemService(NotificationManager::class.java)

            // Foreground Service Channel
            val fgsChannel = NotificationChannel(
                PomodoroService.CHANNEL_ID,
                "Temporizador Pomodoro",
                NotificationManager.IMPORTANCE_LOW
            ).apply {
                description = "Notificación interactiva en segundo plano para el temporizador Pomodoro"
                setShowBadge(false)
            }

            // High Priority Alarm Channel
            val alarmChannel = NotificationChannel(
                AlarmReceiver.CHANNEL_ID,
                "Alertas y Alarmas Pomodoro",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas sonoras y visuales cuando finaliza una sesión o vence una tarea"
                enableVibration(true)
                setShowBadge(true)
            }

            notificationManager?.createNotificationChannels(listOf(fgsChannel, alarmChannel))
        }
    }
}
