package com.viswa2k.eyecare.ui.home

import android.content.Context
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import com.viswa2k.eyecare.data.repository.BreakRepository
import com.viswa2k.eyecare.domain.TimerState
import com.viswa2k.eyecare.service.MonitoringState
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.flowOf
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class HomeViewModelTest {

    private lateinit var monitoringState: MonitoringState
    private lateinit var preferencesManager: PreferencesManager
    private lateinit var breakRepository: BreakRepository
    private lateinit var context: Context

    @Before
    fun setup() {
        monitoringState = MonitoringState()
        preferencesManager = mockk(relaxed = true)
        breakRepository = mockk(relaxed = true)
        context = mockk(relaxed = true)

        every { preferencesManager.isMonitoringEnabled } returns flowOf(false)
        every { breakRepository.getTodayStats() } returns flowOf(null)
        every { breakRepository.getCurrentStreak() } returns flowOf(null)
    }

    @Test
    fun `initial timer state is default`() {
        val state = monitoringState.timerState.value
        assertEquals(TimerState.CYCLE_DURATION_MILLIS, state.remainingMillis)
        assertFalse(state.isRunning)
        assertEquals(0, state.cycleCount)
    }

    @Test
    fun `monitoring state is initially false`() {
        assertFalse(monitoringState.isMonitoring.value)
    }
}
