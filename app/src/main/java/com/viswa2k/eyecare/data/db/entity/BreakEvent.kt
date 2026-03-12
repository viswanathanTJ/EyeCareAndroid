package com.viswa2k.eyecare.data.db.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

enum class BreakEventType {
    TAKEN, SKIPPED, SNOOZED
}

@Entity(tableName = "break_events")
data class BreakEvent(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    val timestamp: Long = System.currentTimeMillis(),
    val type: BreakEventType
)
