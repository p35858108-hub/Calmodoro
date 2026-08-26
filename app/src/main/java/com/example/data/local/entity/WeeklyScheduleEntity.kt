package com.example.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "weekly_schedule")
data class WeeklyScheduleEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val dayOfWeek: Int, // 1 = Lunes, 2 = Martes, 3 = Miércoles, 4 = Jueves, 5 = Viernes, 6 = Sábado, 7 = Domingo
    val title: String,
    val location: String = "",
    val instructorOrTag: String = "", // e.g. "Dr. Ramírez", "Práctica"
    val startTime: String, // "08:00"
    val endTime: String, // "09:30"
    val colorHex: String = "#2DD4BF",
    val isRecess: Boolean = false // e.g. Receso (11:30 - 12:30)
)
