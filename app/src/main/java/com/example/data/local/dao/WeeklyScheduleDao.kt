package com.example.data.local.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Update
import com.example.data.local.entity.WeeklyScheduleEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface WeeklyScheduleDao {
    @Query("SELECT * FROM weekly_schedule WHERE dayOfWeek = :dayOfWeek ORDER BY startTime ASC")
    fun getScheduleForDay(dayOfWeek: Int): Flow<List<WeeklyScheduleEntity>>

    @Query("SELECT * FROM weekly_schedule ORDER BY dayOfWeek ASC, startTime ASC")
    fun getAllSchedule(): Flow<List<WeeklyScheduleEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(schedule: WeeklyScheduleEntity): Long

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(schedules: List<WeeklyScheduleEntity>)

    @Update
    suspend fun update(schedule: WeeklyScheduleEntity)

    @Delete
    suspend fun delete(schedule: WeeklyScheduleEntity)

    @Query("DELETE FROM weekly_schedule WHERE id = :id")
    suspend fun deleteById(id: Long)

    @Query("DELETE FROM weekly_schedule")
    suspend fun deleteAllSchedule()
}
