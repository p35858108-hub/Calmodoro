package com.example.data.repository

import com.example.data.local.dao.UserSettingsDao
import com.example.data.local.entity.UserSettingsEntity
import kotlinx.coroutines.flow.Flow

class SettingsRepository(private val userSettingsDao: UserSettingsDao) {
    val settingsFlow: Flow<UserSettingsEntity?> = userSettingsDao.getSettings()

    suspend fun getSettings(): UserSettingsEntity {
        return userSettingsDao.getSettingsSync() ?: UserSettingsEntity()
    }

    suspend fun updateSettings(settings: UserSettingsEntity) {
        userSettingsDao.insertOrUpdate(settings)
    }

    suspend fun updateFocusDuration(minutes: Int) {
        val current = getSettings()
        userSettingsDao.insertOrUpdate(current.copy(focusDurationMinutes = minutes))
    }

    suspend fun updateShortBreak(minutes: Int) {
        val current = getSettings()
        userSettingsDao.insertOrUpdate(current.copy(shortBreakMinutes = minutes))
    }

    suspend fun updateLongBreak(minutes: Int) {
        val current = getSettings()
        userSettingsDao.insertOrUpdate(current.copy(longBreakMinutes = minutes))
    }

    suspend fun updateSoundChoice(choice: String) {
        val current = getSettings()
        userSettingsDao.insertOrUpdate(current.copy(soundChoice = choice))
    }

    suspend fun updateNotifications(enabled: Boolean) {
        val current = getSettings()
        userSettingsDao.insertOrUpdate(current.copy(notificationsEnabled = enabled))
    }

    suspend fun updateVibration(enabled: Boolean) {
        val current = getSettings()
        userSettingsDao.insertOrUpdate(current.copy(vibrationEnabled = enabled))
    }
}
