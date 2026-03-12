package com.viswa2k.eyecare.domain

import com.viswa2k.eyecare.data.repository.BreakRepository
import kotlinx.coroutines.flow.Flow

class GetStreakUseCase(
    private val repository: BreakRepository
) {
    fun current(): Flow<Int?> = repository.getCurrentStreak()
    fun max(): Flow<Int?> = repository.getMaxStreak()
}
