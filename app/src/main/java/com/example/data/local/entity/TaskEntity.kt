package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val title: String,
    val description: String = "",
    val date: String, // Format: YYYY-MM-DD
    val startTime: String, // Format: HH:mm e.g. "08:30"
    val endTime: String, // Format: HH:mm e.g. "10:00"
    val durationMinutes: Int = 60,
    val colorHex: String = "#2DD4BF",
    val iconName: String = "functions",
    val tag: String = "",
    val isFixed: Boolean = false,
    val repeatMode: String = "NONE", // NONE, DAILY, WEEKLY
    val notifyOnStart: Boolean = false,
    val notifyOnEnd: Boolean = false,
    val isCompleted: Boolean = false,
    val createdAt: Long = System.currentTimeMillis()
)

