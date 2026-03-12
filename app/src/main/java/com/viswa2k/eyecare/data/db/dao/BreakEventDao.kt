package com.viswa2k.eyecare.data.db.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.Query
import com.viswa2k.eyecare.data.db.entity.BreakEvent
import kotlinx.coroutines.flow.Flow

@Dao
interface BreakEventDao {
    @Insert
    suspend fun insert(event: BreakEvent)

    @Query("SELECT * FROM break_events WHERE timestamp BETWEEN :startTime AND :endTime ORDER BY timestamp DESC")
    fun getEventsByDateRange(startTime: Long, endTime: Long): Flow<List<BreakEvent>>

    @Query("SELECT * FROM break_events WHERE timestamp >= :startOfDay ORDER BY timestamp DESC")
    fun getTodayEvents(startOfDay: Long): Flow<List<BreakEvent>>
}
