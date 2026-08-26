package com.example.data.repository

import com.example.data.local.dao.WeeklyScheduleDao
import com.example.data.local.entity.WeeklyScheduleEntity
import kotlinx.coroutines.flow.Flow

class ScheduleRepository(private val weeklyScheduleDao: WeeklyScheduleDao) {
    fun getScheduleForDay(dayOfWeek: Int): Flow<List<WeeklyScheduleEntity>> =
        weeklyScheduleDao.getScheduleForDay(dayOfWeek)

    fun getAllSchedule(): Flow<List<WeeklyScheduleEntity>> =
        weeklyScheduleDao.getAllSchedule()

    suspend fun insert(schedule: WeeklyScheduleEntity): Long =
        weeklyScheduleDao.insert(schedule)

    suspend fun insertAll(schedules: List<WeeklyScheduleEntity>) =
        weeklyScheduleDao.insertAll(schedules)

    suspend fun update(schedule: WeeklyScheduleEntity) =
        weeklyScheduleDao.update(schedule)

    suspend fun delete(schedule: WeeklyScheduleEntity) =
        weeklyScheduleDao.delete(schedule)

    suspend fun deleteById(id: Long) =
        weeklyScheduleDao.deleteById(id)

    suspend fun deleteAllSchedule() =
        weeklyScheduleDao.deleteAllSchedule()
}
