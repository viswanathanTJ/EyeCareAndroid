package com.viswa2k.eyecare.data.db

import androidx.room.Database
import androidx.room.RoomDatabase
import com.viswa2k.eyecare.data.db.dao.BreakEventDao
import com.viswa2k.eyecare.data.db.dao.DailyStatsDao
import com.viswa2k.eyecare.data.db.entity.BreakEvent
import com.viswa2k.eyecare.data.db.entity.DailyStats

@Database(
    entities = [BreakEvent::class, DailyStats::class],
    version = 1,
    exportSchema = false
)
abstract class EyeCareDatabase : RoomDatabase() {
    abstract fun breakEventDao(): BreakEventDao
    abstract fun dailyStatsDao(): DailyStatsDao
}
