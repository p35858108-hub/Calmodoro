package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "pomodoro_sessions")
data class PomodoroSessionEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val taskId: Long? = null,
    val taskTitle: String = "",
    val mode: String = "FOCUS", // FOCUS, SHORT_BREAK, LONG_BREAK
    val durationSeconds: Int,
    val completedAt: Long = System.currentTimeMillis(),
    val wasCompleted: Boolean = true
)
