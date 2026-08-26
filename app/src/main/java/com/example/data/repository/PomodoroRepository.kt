package com.example.data.repository

import com.example.data.local.dao.PomodoroSessionDao
import com.example.data.local.entity.PomodoroSessionEntity
import kotlinx.coroutines.flow.Flow
import java.util.Calendar

class PomodoroRepository(private val pomodoroSessionDao: PomodoroSessionDao) {
    fun getAllSessions(): Flow<List<PomodoroSessionEntity>> =
        pomodoroSessionDao.getAllSessions()

    fun getTodayCompletedCount(): Flow<Int> {
        val calendar = Calendar.getInstance().apply {
            set(Calendar.HOUR_OF_DAY, 0)
            set(Calendar.MINUTE, 0)
            set(Calendar.SECOND, 0)
            set(Calendar.MILLISECOND, 0)
        }
        return pomodoroSessionDao.getTodayCompletedCount(calendar.timeInMillis)
    }

    suspend fun recordSession(session: PomodoroSessionEntity): Long =
        pomodoroSessionDao.insertSession(session)

    suspend fun clearHistory() =
        pomodoroSessionDao.clearAll()
}
