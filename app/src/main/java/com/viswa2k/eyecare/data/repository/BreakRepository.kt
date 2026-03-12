package com.viswa2k.eyecare.data.repository

import com.viswa2k.eyecare.data.db.entity.BreakEvent
import com.viswa2k.eyecare.data.db.entity.BreakEventType
import com.viswa2k.eyecare.data.db.entity.DailyStats
import kotlinx.coroutines.flow.Flow

interface BreakRepository {
    suspend fun recordBreakEvent(type: BreakEventType)
    fun getTodayEvents(): Flow<List<BreakEvent>>
    fun getTodayStats(): Flow<DailyStats?>
    fun getWeeklyStats(): Flow<List<DailyStats>>
    fun getCurrentStreak(): Flow<Int?>
    fun getMaxStreak(): Flow<Int?>
}
