package com.example.ui.pomodoro

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.CalmodoroApp
import com.example.audio.AmbientSound
import com.example.audio.AmbientSoundPlayer
import com.example.data.local.entity.TaskEntity
import com.example.timer.PomodoroEngine
import com.example.timer.PomodoroMode
import com.example.timer.PomodoroState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class PomodoroViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CalmodoroApp
    private val engine = PomodoroEngine.getInstance(application)
    private val settingsRepo = app.settingsRepository
    private val pomodoroRepo = app.pomodoroRepository
    private val taskRepo = app.taskRepository

    val timerState: StateFlow<PomodoroState> = engine.stateFlow
    val ambientSound: StateFlow<AmbientSound> = AmbientSoundPlayer.currentSound

    val todayCompletedCount: StateFlow<Int> = pomodoroRepo.getTodayCompletedCount()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = 0
        )

    val allTasks: StateFlow<List<TaskEntity>> = taskRepo.getAllTasks()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    fun setMode(mode: PomodoroMode) {
        viewModelScope.launch {
            val settings = settingsRepo.getSettings()
            val durationMinutes = when (mode) {
                PomodoroMode.FOCUS -> settings.focusDurationMinutes
                PomodoroMode.SHORT_BREAK -> settings.shortBreakMinutes
                PomodoroMode.LONG_BREAK -> settings.longBreakMinutes
            }
            engine.setMode(
                mode = mode,
                durationMinutes = durationMinutes,
                taskId = timerState.value.activeTaskId,
                taskTitle = timerState.value.activeTaskTitle
            )
        }
    }

    fun startOrResume() {
        engine.startOrResume()
    }

    fun pause() {
        engine.pause()
    }

    fun reset() {
        engine.reset()
    }

    fun addMinutes(deltaMinutes: Int) {
        engine.addMinutes(deltaMinutes)
    }

    fun skipMode() {
        engine.skipMode()
    }

    fun setAmbientSound(sound: AmbientSound) {
        AmbientSoundPlayer.setSound(sound)
    }

    fun attachTask(task: TaskEntity?) {
        if (task != null) {
            engine.setMode(
                mode = PomodoroMode.FOCUS,
                durationMinutes = task.durationMinutes.coerceIn(5, 120),
                taskId = task.id,
                taskTitle = task.title
            )
        } else {
            viewModelScope.launch {
                val settings = settingsRepo.getSettings()
                engine.setMode(
                    mode = PomodoroMode.FOCUS,
                    durationMinutes = settings.focusDurationMinutes,
                    taskId = null,
                    taskTitle = ""
                )
            }
        }
    }

    fun setCustomFocusGoal(title: String, durationMinutes: Int = 25) {
        engine.setMode(
            mode = PomodoroMode.FOCUS,
            durationMinutes = durationMinutes.coerceIn(5, 180),
            taskId = null,
            taskTitle = title.trim()
        )
    }

    fun markCurrentTaskCompleted() {
        val taskId = timerState.value.activeTaskId ?: return
        viewModelScope.launch {
            taskRepo.setCompleted(taskId, true)
        }
    }
}

