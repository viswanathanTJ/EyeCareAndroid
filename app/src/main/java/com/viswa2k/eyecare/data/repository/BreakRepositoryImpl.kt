package com.viswa2k.eyecare.data.repository

import com.viswa2k.eyecare.data.db.dao.BreakEventDao
import com.viswa2k.eyecare.data.db.dao.DailyStatsDao
import com.viswa2k.eyecare.data.db.entity.BreakEvent
import com.viswa2k.eyecare.data.db.entity.BreakEventType
import com.viswa2k.eyecare.data.db.entity.DailyStats
import kotlinx.coroutines.flow.Flow
import java.time.LocalDate
import java.time.LocalTime
import java.time.ZoneId
import java.time.format.DateTimeFormatter

class BreakRepositoryImpl(
    private val breakEventDao: BreakEventDao,
    private val dailyStatsDao: DailyStatsDao
) : BreakRepository {

    private val dateFormatter = DateTimeFormatter.ISO_LOCAL_DATE

    override suspend fun recordBreakEvent(type: BreakEventType) {
        val event = BreakEvent(type = type)
        breakEventDao.insert(event)

        val today = LocalDate.now().format(dateFormatter)
        val existing = dailyStatsDao.getByDate(today) ?: DailyStats(date = today)

        val updated = when (type) {
            BreakEventType.TAKEN -> existing.copy(
                breaksTaken = existing.breaksTaken + 1,
                totalCycles = existing.totalCycles + 1
            )
            BreakEventType.SKIPPED -> existing.copy(
                breaksSkipped = existing.breaksSkipped + 1,
                totalCycles = existing.totalCycles + 1
            )
            BreakEventType.SNOOZED -> existing.copy(
                totalCycles = existing.totalCycles + 1
            )
        }

        // Calculate streak
        val yesterday = LocalDate.now().minusDays(1).format(dateFormatter)
        val yesterdayStreak = dailyStatsDao.getStreakForDate(yesterday) ?: 0
        val streak = if (updated.breaksTaken > 0) yesterdayStreak + 1 else 0

        dailyStatsDao.upsert(updated.copy(streakDays = streak))
    }

    override fun getTodayEvents(): Flow<List<BreakEvent>> {
        val startOfDay = LocalDate.now()
            .atTime(LocalTime.MIN)
            .atZone(ZoneId.systemDefault())
            .toInstant()
            .toEpochMilli()
        return breakEventDao.getTodayEvents(startOfDay)
    }

    override fun getTodayStats(): Flow<DailyStats?> {
        return dailyStatsDao.observeByDate(LocalDate.now().format(dateFormatter))
    }

    override fun getWeeklyStats(): Flow<List<DailyStats>> {
        return dailyStatsDao.getRecentDays(7)
    }

    override fun getCurrentStreak(): Flow<Int?> {
        val today = LocalDate.now().format(dateFormatter)
        return dailyStatsDao.observeByDate(today).let { flow ->
            kotlinx.coroutines.flow.flow {
                flow.collect { stats ->
                    emit(stats?.streakDays)
                }
            }
        }
    }

    override fun getMaxStreak(): Flow<Int?> {
        return dailyStatsDao.getMaxStreak()
    }
}
