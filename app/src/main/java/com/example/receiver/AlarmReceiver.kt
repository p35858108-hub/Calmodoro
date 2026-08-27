package com.example.receiver

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import androidx.core.app.NotificationCompat
import com.example.MainActivity
import com.example.R
import com.example.audio.SoundManager
import com.example.timer.PomodoroEngine

class AlarmReceiver : BroadcastReceiver() {

    companion object {
        const val CHANNEL_ID = "calmodoro_alarm_channel"
        const val TASK_CHANNEL_ID = "calmodoro_tasks_channel"
        const val EXTRA_TITLE = "extra_title"
        const val EXTRA_MESSAGE = "extra_message"
        const val EXTRA_SOUND_CHOICE = "extra_sound_choice"
        const val EXTRA_VIBRATION = "extra_vibration"
        const val NOTIFICATION_ID = 2001
    }

    override fun onReceive(context: Context, intent: Intent) {
        val action = intent.action

        if (action == TaskAlarmScheduler.ACTION_TASK_START || action == TaskAlarmScheduler.ACTION_TASK_END) {
            val taskId = intent.getLongExtra(TaskAlarmScheduler.EXTRA_TASK_ID, 0L)
            val taskTitle = intent.getStringExtra(TaskAlarmScheduler.EXTRA_TASK_TITLE) ?: "Tarea"
            val taskDesc = intent.getStringExtra(TaskAlarmScheduler.EXTRA_TASK_DESC) ?: ""
            val isStart = action == TaskAlarmScheduler.ACTION_TASK_START

            // Play notification tone and vibration
            try {
                SoundManager.getInstance(context).playFinish("digital_bell")
            } catch (e: Exception) {
                e.printStackTrace()
            }
            vibrateAlert(context)

            val title = if (isStart) "⏰ ¡Hora de comenzar: $taskTitle!" else "🏁 Tarea finalizada: $taskTitle"
            val message = if (isStart) {
                if (taskDesc.isNotEmpty()) "$taskDesc • Pulsa para abrir tu sesión" else "Es el momento planificado para esta tarea."
            } else {
                "El tiempo asignado para esta tarea ha finalizado."
            }

            showTaskNotification(context, (taskId + (if (isStart) 5000 else 6000)).toInt(), title, message)
            return
        }

        val title = intent.getStringExtra(EXTRA_TITLE) ?: "¡Tiempo cumplido!"
        val message = intent.getStringExtra(EXTRA_MESSAGE) ?: "Tu sesión de Pomodoro ha finalizado."
        val vibrationEnabled = intent.getBooleanExtra(EXTRA_VIBRATION, true)

        // 1. Vibrate gently if enabled
        if (vibrationEnabled) {
            vibrateAlert(context)
        }

        // 2. Show Heads-up Alert Notification (clean, non-looping)
        showAlarmNotification(context, title, message)

        // 3. Notify Engine to complete session and trigger gentle finish audio
        PomodoroEngine.getInstance(context).onAlarmTriggered()
    }

    private fun vibrateAlert(context: Context) {
        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                val vibratorManager = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as? VibratorManager
                val vibrator = vibratorManager?.defaultVibrator
                val pattern = longArrayOf(0, 300, 150, 300)
                vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
            } else {
                @Suppress("DEPRECATION")
                val vibrator = context.getSystemService(Context.VIBRATOR_SERVICE) as? Vibrator
                val pattern = longArrayOf(0, 300, 150, 300)
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
                    vibrator?.vibrate(VibrationEffect.createWaveform(pattern, -1))
                } else {
                    @Suppress("DEPRECATION")
                    vibrator?.vibrate(pattern, -1)
                }
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun showTaskNotification(context: Context, notificationId: Int, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                TASK_CHANNEL_ID,
                "Recordatorios de Tareas",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Notificaciones de inicio y fin de tareas programadas"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, TASK_CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_REMINDER)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(notificationId, notification)
    }

    private fun showAlarmNotification(context: Context, title: String, message: String) {
        val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val channel = NotificationChannel(
                CHANNEL_ID,
                "Alarmas de Pomodoro",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Alertas de finalización de sesiones y tareas"
                enableVibration(true)
                setShowBadge(true)
            }
            notificationManager.createNotificationChannel(channel)
        }

        val openAppIntent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TOP
        }
        val pendingIntent = PendingIntent.getActivity(
            context,
            0,
            openAppIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(title)
            .setContentText(message)
            .setPriority(NotificationCompat.PRIORITY_HIGH)
            .setCategory(NotificationCompat.CATEGORY_EVENT)
            .setOnlyAlertOnce(true)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .build()

        notificationManager.notify(NOTIFICATION_ID, notification)
    }
}
