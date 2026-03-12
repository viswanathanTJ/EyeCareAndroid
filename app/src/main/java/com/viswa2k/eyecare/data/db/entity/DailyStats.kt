package com.viswa2k.eyecare.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "daily_stats")
data class DailyStats(
    @PrimaryKey val date: String, // yyyy-MM-dd
    val breaksTaken: Int = 0,
    val breaksSkipped: Int = 0,
    val totalCycles: Int = 0,
    val streakDays: Int = 0
)
