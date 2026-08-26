package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "user_settings")
data class UserSettingsEntity(
    @PrimaryKey
    val id: Int = 1,
    val focusDurationMinutes: Int = 25,
    val shortBreakMinutes: Int = 5,
    val longBreakMinutes: Int = 15,
    val soundChoice: String = "digital_bell", // digital_bell, wind, lofi, minimal
    val notificationsEnabled: Boolean = true,
    val vibrationEnabled: Boolean = true
)
