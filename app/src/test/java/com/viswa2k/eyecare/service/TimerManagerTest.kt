package com.viswa2k.eyecare.service

import com.viswa2k.eyecare.domain.TimerState
import org.junit.Assert.assertEquals
import org.junit.Assert.assertFalse
import org.junit.Before
import org.junit.Test

class TimerManagerTest {

    private lateinit var monitoringState: MonitoringState
    private lateinit var timerManager: TimerManager

    @Before
    fun setup() {
        monitoringState = MonitoringState()
        timerManager = TimerManager(monitoringState)
    }

    @Test
    fun `initial state has default cycle duration`() {
        val state = monitoringState.timerState.value
        assertEquals(TimerState.CYCLE_DURATION_MILLIS, state.remainingMillis)
        assertFalse(state.isRunning)
        assertEquals(0, state.cycleCount)
    }

    @Test
    fun `reset restores timer to full duration`() {
        monitoringState.updateTimerState(
            TimerState(remainingMillis = 5000, isRunning = true, cycleCount = 3)
        )
        timerManager.reset()

        val state = monitoringState.timerState.value
        assertEquals(TimerState.CYCLE_DURATION_MILLIS, state.remainingMillis)
        assertFalse(state.isRunning)
        assertEquals(3, state.cycleCount) // cycle count preserved
    }

    @Test
    fun `setCycleDuration changes duration`() {
        timerManager.setCycleDuration(10)
        timerManager.reset()

        val state = monitoringState.timerState.value
        assertEquals(10 * 60 * 1000L, state.remainingMillis)
    }

    @Test
    fun `cancel stops timer`() {
        timerManager.cancel()
        // Should not throw
    }
}
