package com.viswa2k.eyecare.domain

import com.viswa2k.eyecare.data.repository.BreakRepository
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import kotlinx.coroutines.flow.flowOf
import org.junit.Test

class GetStreakUseCaseTest {

    private val repository: BreakRepository = mockk()
    private val useCase = GetStreakUseCase(repository)

    @Test
    fun `current delegates to repository`() {
        every { repository.getCurrentStreak() } returns flowOf(5)
        useCase.current()
        verify { repository.getCurrentStreak() }
    }

    @Test
    fun `max delegates to repository`() {
        every { repository.getMaxStreak() } returns flowOf(10)
        useCase.max()
        verify { repository.getMaxStreak() }
    }
}
