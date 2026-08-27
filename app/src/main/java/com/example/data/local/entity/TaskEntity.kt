package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String, // Format: YYYY-MM-DD (Start / base date)
    val startTime: String, // Format: HH:mm e.g. "08:30"
    val endTime: String, // Format: HH:mm e.g. "10:00"
    val durationMinutes: Int = 60,
    val colorHex: String = "#2DD4BF",
    val iconName: String = "functions",
    val tag: String = "",
    val isFixed: Boolean = false,
    val repeatMode: String = "NONE", // NONE, DAILY, WEEKLY, WEEKDAYS
    val notifyOnStart: Boolean = false,
    val notifyOnEnd: Boolean = false,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

fun TaskEntity.matchesDate(dateIso: String, calendar: Calendar? = null): Boolean {
    val targetCal = calendar ?: run {
        val cal = Calendar.getInstance()
        try {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val d = sdf.parse(dateIso)
            if (d != null) cal.time = d
        } catch (e: Exception) {
            // ignore
        }
        cal
    }

    return when (repeatMode.uppercase()) {
        "DAILY" -> {
            // Repeats every day from start date onward
            date <= dateIso
        }
        "WEEKDAYS" -> {
            val dayOfWeek = targetCal.get(Calendar.DAY_OF_WEEK)
            val isWeekday = dayOfWeek != Calendar.SATURDAY && dayOfWeek != Calendar.SUNDAY
            isWeekday && (date <= dateIso)
        }
        "WEEKLY" -> {
            // Repeats weekly on the same day of the week
            val taskCal = Calendar.getInstance().apply {
                try {
                    val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
                    val d = sdf.parse(date)
                    if (d != null) time = d
                } catch (e: Exception) {
                    time = Date()
                }
            }
            val taskDayOfWeek = taskCal.get(Calendar.DAY_OF_WEEK)
            val targetDayOfWeek = targetCal.get(Calendar.DAY_OF_WEEK)
            (taskDayOfWeek == targetDayOfWeek) && (date <= dateIso)
        }
        else -> {
            // Single date
            date == dateIso
        }
    }
}

