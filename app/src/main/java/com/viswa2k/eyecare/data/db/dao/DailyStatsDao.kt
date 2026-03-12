package com.viswa2k.eyecare.data.db.dao

import androidx.room.Dao
import androidx.room.Query
import androidx.room.Upsert
import com.viswa2k.eyecare.data.db.entity.DailyStats
import kotlinx.coroutines.flow.Flow

@Dao
interface DailyStatsDao {
    @Upsert
    suspend fun upsert(stats: DailyStats)

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    suspend fun getByDate(date: String): DailyStats?

    @Query("SELECT * FROM daily_stats WHERE date = :date")
    fun observeByDate(date: String): Flow<DailyStats?>

    @Query("SELECT * FROM daily_stats ORDER BY date DESC LIMIT :days")
    fun getRecentDays(days: Int): Flow<List<DailyStats>>

    @Query("SELECT MAX(streakDays) FROM daily_stats")
    fun getMaxStreak(): Flow<Int?>

    @Query("SELECT streakDays FROM daily_stats WHERE date = :date")
    suspend fun getStreakForDate(date: String): Int?
}
