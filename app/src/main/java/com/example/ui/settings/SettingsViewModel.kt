package com.example.ui.settings

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.CalmodoroApp
import com.example.audio.SoundManager
import com.example.data.local.entity.UserSettingsEntity
import com.example.timer.PomodoroEngine
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(application: Application) : AndroidViewModel(application) {

    private val app = application as CalmodoroApp
    private val repository = app.settingsRepository
    private val taskRepo = app.taskRepository
    private val scheduleRepo = app.scheduleRepository
    private val pomodoroRepo = app.pomodoroRepository
    private val soundManager = SoundManager.getInstance(application)
    private val engine = PomodoroEngine.getInstance(application)

    val settingsState: StateFlow<UserSettingsEntity> = repository.settingsFlow
        .map { it ?: UserSettingsEntity() }
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserSettingsEntity()
        )

    fun clearAllTasks(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            taskRepo.deleteAllTasks()
            onDone()
        }
    }

    fun clearAllSchedule(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            scheduleRepo.deleteAllSchedule()
            onDone()
        }
    }

    fun clearPomodoroHistory(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            pomodoroRepo.clearHistory()
            onDone()
        }
    }

    fun resetAllData(onDone: () -> Unit = {}) {
        viewModelScope.launch {
            taskRepo.deleteAllTasks()
            scheduleRepo.deleteAllSchedule()
            pomodoroRepo.clearHistory()
            engine.reset()
            onDone()
        }
    }

    fun updateFocusDuration(minutes: Int) {
        viewModelScope.launch {
            repository.updateFocusDuration(minutes)
            syncEngine()
        }
    }

    fun updateShortBreak(minutes: Int) {
        viewModelScope.launch {
            repository.updateShortBreak(minutes)
            syncEngine()
        }
    }

    fun updateLongBreak(minutes: Int) {
        viewModelScope.launch {
            repository.updateLongBreak(minutes)
            syncEngine()
        }
    }

    fun updateSoundChoice(choice: String) {
        viewModelScope.launch {
            repository.updateSoundChoice(choice)
            syncEngine()
            soundManager.previewSound(choice)
        }
    }

    fun previewSound(choice: String) {
        soundManager.previewSound(choice)
    }

    fun updateNotifications(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateNotifications(enabled)
            syncEngine()
        }
    }

    fun updateVibration(enabled: Boolean) {
        viewModelScope.launch {
            repository.updateVibration(enabled)
            syncEngine()
        }
    }

    private suspend fun syncEngine() {
        val s = repository.getSettings()
        engine.updateSettings(
            soundChoice = s.soundChoice,
            notifications = s.notificationsEnabled,
            vibration = s.vibrationEnabled,
            focusMinutes = s.focusDurationMinutes,
            shortMinutes = s.shortBreakMinutes,
            longMinutes = s.longBreakMinutes
        )
    }
}
