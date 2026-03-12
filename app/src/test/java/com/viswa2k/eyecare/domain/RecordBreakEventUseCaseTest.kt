package com.viswa2k.eyecare.domain

import com.viswa2k.eyecare.data.db.entity.BreakEventType
import com.viswa2k.eyecare.data.repository.BreakRepository
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Test

class RecordBreakEventUseCaseTest {

    private val repository: BreakRepository = mockk(relaxed = true)
    private val useCase = RecordBreakEventUseCase(repository)

    @Test
    fun `invoke delegates to repository`() = runTest {
        useCase(BreakEventType.TAKEN)
        coVerify { repository.recordBreakEvent(BreakEventType.TAKEN) }
    }

    @Test
    fun `invoke with SKIPPED delegates to repository`() = runTest {
        useCase(BreakEventType.SKIPPED)
        coVerify { repository.recordBreakEvent(BreakEventType.SKIPPED) }
    }

    @Test
    fun `invoke with SNOOZED delegates to repository`() = runTest {
        useCase(BreakEventType.SNOOZED)
        coVerify { repository.recordBreakEvent(BreakEventType.SNOOZED) }
    }
}
