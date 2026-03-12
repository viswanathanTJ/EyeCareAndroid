package com.viswa2k.eyecare.domain

import com.viswa2k.eyecare.data.db.entity.BreakEventType
import com.viswa2k.eyecare.data.repository.BreakRepository

class RecordBreakEventUseCase(
    private val repository: BreakRepository
) {
    suspend operator fun invoke(type: BreakEventType) {
        repository.recordBreakEvent(type)
    }
}
