package com.example.ui.calendar

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.CalmodoroApp
import com.example.data.local.entity.TaskEntity
import com.example.receiver.TaskAlarmScheduler
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

enum class CalendarViewMode {
    DAY, WEEK, MONTH
}

class CalendarViewModel(application: Application) : AndroidViewModel(application) {

    private val repository = (application as CalmodoroApp).taskRepository
    private val appContext = application.applicationContext

    private val sdfIso = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
    private val sdfDisplay = SimpleDateFormat("EEEE, d MMM", Locale("es", "ES"))
    private val sdfMonthYear = SimpleDateFormat("MMMM yyyy", Locale("es", "ES"))

    private val _selectedDateCalendar = MutableStateFlow(Calendar.getInstance())
    val selectedDateCalendar: StateFlow<Calendar> = _selectedDateCalendar.asStateFlow()

    private val _selectedDateIso = MutableStateFlow(sdfIso.format(Date()))
    val selectedDateIso: StateFlow<String> = _selectedDateIso.asStateFlow()

    private val _viewMode = MutableStateFlow(CalendarViewMode.DAY)
    val viewMode: StateFlow<CalendarViewMode> = _viewMode.asStateFlow()

    // Current viewing month for Month View
    private val _viewingMonthCalendar = MutableStateFlow(Calendar.getInstance())
    val viewingMonthCalendar: StateFlow<Calendar> = _viewingMonthCalendar.asStateFlow()

    val tasksForDate: StateFlow<List<TaskEntity>> = _selectedDateIso
        .flatMapLatest { dateStr -> repository.getTasksForDate(dateStr) }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val allTasks: StateFlow<List<TaskEntity>> = repository.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setViewMode(mode: CalendarViewMode) {
        _viewMode.value = mode
    }

    fun selectDate(cal: Calendar) {
        val newCal = Calendar.getInstance().apply {
            timeInMillis = cal.timeInMillis
        }
        _selectedDateCalendar.value = newCal
        _selectedDateIso.value = sdfIso.format(newCal.time)
        // Also update viewing month so month grid reflects it
        val newMonthCal = Calendar.getInstance().apply {
            timeInMillis = cal.timeInMillis
            set(Calendar.DAY_OF_MONTH, 1)
        }
        _viewingMonthCalendar.value = newMonthCal
    }

    fun selectDateIso(iso: String) {
        try {
            val parsed = sdfIso.parse(iso)
            if (parsed != null) {
                val cal = Calendar.getInstance().apply { time = parsed }
                selectDate(cal)
            }
        } catch (e: Exception) {
            _selectedDateIso.value = iso
        }
    }

    fun getFormattedDateTitle(cal: Calendar): Pair<String, String> {
        val todayCal = Calendar.getInstance()
        val isToday = cal.get(Calendar.YEAR) == todayCal.get(Calendar.YEAR) &&
                cal.get(Calendar.DAY_OF_YEAR) == todayCal.get(Calendar.DAY_OF_YEAR)

        val headerMain = if (isToday) "Hoy" else {
            val dayName = SimpleDateFormat("EEEE", Locale("es", "ES")).format(cal.time)
            dayName.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
        }

        val rawDateStr = sdfDisplay.format(cal.time)
        val subtitle = rawDateStr.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }

        return headerMain to subtitle
    }

    fun getFormattedMonthTitle(cal: Calendar): String {
        val raw = sdfMonthYear.format(cal.time)
        return raw.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
    }

    fun previousDay() {
        val newCal = Calendar.getInstance().apply {
            timeInMillis = _selectedDateCalendar.value.timeInMillis
            add(Calendar.DAY_OF_YEAR, -1)
        }
        _selectedDateCalendar.value = newCal
        _selectedDateIso.value = sdfIso.format(newCal.time)
    }

    fun nextDay() {
        val newCal = Calendar.getInstance().apply {
            timeInMillis = _selectedDateCalendar.value.timeInMillis
            add(Calendar.DAY_OF_YEAR, 1)
        }
        _selectedDateCalendar.value = newCal
        _selectedDateIso.value = sdfIso.format(newCal.time)
    }

    fun previousWeek() {
        val newCal = Calendar.getInstance().apply {
            timeInMillis = _selectedDateCalendar.value.timeInMillis
            add(Calendar.WEEK_OF_YEAR, -1)
        }
        _selectedDateCalendar.value = newCal
        _selectedDateIso.value = sdfIso.format(newCal.time)
    }

    fun nextWeek() {
        val newCal = Calendar.getInstance().apply {
            timeInMillis = _selectedDateCalendar.value.timeInMillis
            add(Calendar.WEEK_OF_YEAR, 1)
        }
        _selectedDateCalendar.value = newCal
        _selectedDateIso.value = sdfIso.format(newCal.time)
    }

    fun previousMonth() {
        val newCal = Calendar.getInstance().apply {
            timeInMillis = _viewingMonthCalendar.value.timeInMillis
            add(Calendar.MONTH, -1)
        }
        _viewingMonthCalendar.value = newCal
    }

    fun nextMonth() {
        val newCal = Calendar.getInstance().apply {
            timeInMillis = _viewingMonthCalendar.value.timeInMillis
            add(Calendar.MONTH, 1)
        }
        _viewingMonthCalendar.value = newCal
    }

    fun addTask(task: TaskEntity) {
        viewModelScope.launch {
            val id = repository.insertTask(task)
            val savedTask = task.copy(id = id)
            TaskAlarmScheduler.scheduleTaskAlarms(appContext, savedTask)
        }
    }

    fun updateTask(task: TaskEntity) {
        viewModelScope.launch {
            repository.updateTask(task)
            TaskAlarmScheduler.scheduleTaskAlarms(appContext, task)
        }
    }

    fun toggleTaskCompletion(task: TaskEntity) {
        viewModelScope.launch {
            val newCompleted = !task.isCompleted
            repository.setCompleted(task.id, newCompleted)
            if (newCompleted) {
                TaskAlarmScheduler.cancelTaskAlarms(appContext, task.id)
            }
        }
    }

    fun deleteTask(task: TaskEntity) {
        viewModelScope.launch {
            TaskAlarmScheduler.cancelTaskAlarms(appContext, task.id)
            repository.deleteTask(task)
        }
    }
}
