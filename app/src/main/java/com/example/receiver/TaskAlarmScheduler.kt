package com.example.receiver

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import com.example.data.local.entity.TaskEntity
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

object TaskAlarmScheduler {

    const val ACTION_TASK_START = "com.example.ACTION_TASK_START"
    const val ACTION_TASK_END = "com.example.ACTION_TASK_END"

    const val EXTRA_TASK_ID = "extra_task_id"
    const val EXTRA_TASK_TITLE = "extra_task_title"
    const val EXTRA_TASK_DESC = "extra_task_desc"
    const val EXTRA_TASK_TYPE = "extra_task_type"

    fun scheduleTaskAlarms(context: Context, task: TaskEntity) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return

        // 1. Schedule Start Alarm if requested
        if (task.notifyOnStart) {
            val startMillis = parseDateTimeToMillis(task.date, task.startTime)
            if (startMillis > System.currentTimeMillis()) {
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    action = ACTION_TASK_START
                    putExtra(EXTRA_TASK_ID, task.id)
                    putExtra(EXTRA_TASK_TITLE, task.title)
                    putExtra(EXTRA_TASK_DESC, task.description)
                    putExtra(EXTRA_TASK_TYPE, "START")
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    getStartRequestCode(task.id),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startMillis, pendingIntent)
                        } else {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startMillis, pendingIntent)
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, startMillis, pendingIntent)
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, startMillis, pendingIntent)
                    }
                } catch (e: Exception) {
                    Log.e("TaskAlarmScheduler", "Error scheduling start alarm", e)
                }
            }
        } else {
            cancelStartAlarm(context, task.id)
        }

        // 2. Schedule End Alarm if requested
        if (task.notifyOnEnd) {
            val endMillis = parseDateTimeToMillis(task.date, task.endTime)
            if (endMillis > System.currentTimeMillis()) {
                val intent = Intent(context, AlarmReceiver::class.java).apply {
                    action = ACTION_TASK_END
                    putExtra(EXTRA_TASK_ID, task.id)
                    putExtra(EXTRA_TASK_TITLE, task.title)
                    putExtra(EXTRA_TASK_DESC, task.description)
                    putExtra(EXTRA_TASK_TYPE, "END")
                }
                val pendingIntent = PendingIntent.getBroadcast(
                    context,
                    getEndRequestCode(task.id),
                    intent,
                    PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
                )

                try {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                        if (alarmManager.canScheduleExactAlarms()) {
                            alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endMillis, pendingIntent)
                        } else {
                            alarmManager.setAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endMillis, pendingIntent)
                        }
                    } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        alarmManager.setExactAndAllowWhileIdle(AlarmManager.RTC_WAKEUP, endMillis, pendingIntent)
                    } else {
                        alarmManager.setExact(AlarmManager.RTC_WAKEUP, endMillis, pendingIntent)
                    }
                } catch (e: Exception) {
                    Log.e("TaskAlarmScheduler", "Error scheduling end alarm", e)
                }
            }
        } else {
            cancelEndAlarm(context, task.id)
        }
    }

    fun cancelTaskAlarms(context: Context, taskId: Long) {
        cancelStartAlarm(context, taskId)
        cancelEndAlarm(context, taskId)
    }

    private fun cancelStartAlarm(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AlarmReceiver::class.java).apply { action = ACTION_TASK_START }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getStartRequestCode(taskId),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun cancelEndAlarm(context: Context, taskId: Long) {
        val alarmManager = context.getSystemService(Context.ALARM_SERVICE) as? AlarmManager ?: return
        val intent = Intent(context, AlarmReceiver::class.java).apply { action = ACTION_TASK_END }
        val pendingIntent = PendingIntent.getBroadcast(
            context,
            getEndRequestCode(taskId),
            intent,
            PendingIntent.FLAG_NO_CREATE or PendingIntent.FLAG_IMMUTABLE
        )
        if (pendingIntent != null) {
            alarmManager.cancel(pendingIntent)
            pendingIntent.cancel()
        }
    }

    private fun getStartRequestCode(taskId: Long): Int = (taskId * 2 + 10000).toInt()
    private fun getEndRequestCode(taskId: Long): Int = (taskId * 2 + 10001).toInt()

    private fun parseDateTimeToMillis(dateStr: String, timeStr: String): Long {
        return try {
            val sdf = SimpleDateFormat("yyyy-MM-dd HH:mm", Locale.getDefault())
            val date = sdf.parse("$dateStr $timeStr")
            date?.time ?: 0L
        } catch (e: Exception) {
            0L
        }
    }
}
