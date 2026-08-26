package com.example.ui.schedule

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.CalmodoroApp
import com.example.data.local.entity.TaskEntity
import com.example.data.local.entity.WeeklyScheduleEntity
import com.example.ui.components.calculateMinutesDiff
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Date
import java.util.Locale

class ScheduleViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CalmodoroApp
    private val scheduleRepo = app.scheduleRepository
    private val taskRepo = app.taskRepository

    // Current day of week (Calendar: 1=Sun, 2=Mon... convert to 1=Mon, 2=Tue... 7=Sun)
    private val currentDayOfWeek: Int = run {
        val calDay = Calendar.getInstance().get(Calendar.DAY_OF_WEEK)
        when (calDay) {
            Calendar.MONDAY -> 1
            Calendar.TUESDAY -> 2
            Calendar.WEDNESDAY -> 3
            Calendar.THURSDAY -> 4
            Calendar.FRIDAY -> 5
            Calendar.SATURDAY -> 6
            Calendar.SUNDAY -> 7
            else -> 1
        }
    }

    private val _selectedDay = MutableStateFlow(currentDayOfWeek)
    val selectedDay: StateFlow<Int> = _selectedDay.asStateFlow()

    val allSchedule: StateFlow<List<WeeklyScheduleEntity>> = scheduleRepo.getAllSchedule()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val scheduleForSelectedDay: StateFlow<List<WeeklyScheduleEntity>> = _selectedDay
        .flatMapLatest { dayInt -> scheduleRepo.getScheduleForDay(dayInt) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun selectDay(day: Int) {
        _selectedDay.value = day
    }

    fun addSchedule(schedule: WeeklyScheduleEntity) {
        viewModelScope.launch {
            scheduleRepo.insert(schedule)
        }
    }

    fun updateSchedule(schedule: WeeklyScheduleEntity) {
        viewModelScope.launch {
            scheduleRepo.update(schedule)
        }
    }

    fun deleteSchedule(schedule: WeeklyScheduleEntity) {
        viewModelScope.launch {
            scheduleRepo.delete(schedule)
        }
    }

    fun importScheduleToToday(onSuccess: (Int) -> Unit) {
        viewModelScope.launch {
            val sdf = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            val todayDate = sdf.format(Date())
            val classes = scheduleForSelectedDay.value.filter { !it.isRecess }

            var count = 0
            for (c in classes) {
                val dur = calculateMinutesDiff(c.startTime, c.endTime)
                taskRepo.insertTask(
                    TaskEntity(
                        title = c.title,
                        description = "${c.location} ${if (c.instructorOrTag.isNotEmpty()) "• " + c.instructorOrTag else ""}".trim(),
                        date = todayDate,
                        startTime = c.startTime,
                        endTime = c.endTime,
                        durationMinutes = dur,
                        colorHex = c.colorHex,
                        iconName = "book",
                        tag = "Clase"
                    )
                )
                count++
            }
            onSuccess(count)
        }
    }
}
