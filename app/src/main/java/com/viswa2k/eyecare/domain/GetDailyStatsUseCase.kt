package com.viswa2k.eyecare.domain

import com.viswa2k.eyecare.data.db.entity.DailyStats
import com.viswa2k.eyecare.data.repository.BreakRepository
import kotlinx.coroutines.flow.Flow

class GetDailyStatsUseCase(
    private val repository: BreakRepository
) {
    fun today(): Flow<DailyStats?> = repository.getTodayStats()
    fun weekly(): Flow<List<DailyStats>> = repository.getWeeklyStats()
}
