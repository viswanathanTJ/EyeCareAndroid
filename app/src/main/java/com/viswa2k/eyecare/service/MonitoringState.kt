package com.viswa2k.eyecare.service

import com.viswa2k.eyecare.domain.TimerState
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class MonitoringState {

    private val _timerState = MutableStateFlow(TimerState())
    val timerState: StateFlow<TimerState> = _timerState.asStateFlow()

    private val _isMonitoring = MutableStateFlow(false)
    val isMonitoring: StateFlow<Boolean> = _isMonitoring.asStateFlow()

    // Live screen-on time in millis, updated every second by the service
    private val _screenOnTimeToday = MutableStateFlow(0L)
    val screenOnTimeToday: StateFlow<Long> = _screenOnTimeToday.asStateFlow()

    private var accumulatedScreenTime: Long = 0L
    private var screenOnStartTime: Long = 0L

    fun updateTimerState(state: TimerState) {
        _timerState.value = state
    }

    fun setMonitoring(monitoring: Boolean) {
        _isMonitoring.value = monitoring
    }

    fun resetTimer() {
        _timerState.value = TimerState(
            cycleCount = _timerState.value.cycleCount
        )
    }

    fun onScreenOn() {
        screenOnStartTime = System.currentTimeMillis()
    }

    fun onScreenOff() {
        if (screenOnStartTime > 0) {
            accumulatedScreenTime += System.currentTimeMillis() - screenOnStartTime
            screenOnStartTime = 0L
            _screenOnTimeToday.value = accumulatedScreenTime
        }
    }

    fun tickScreenTime() {
        if (screenOnStartTime > 0) {
            _screenOnTimeToday.value = accumulatedScreenTime + (System.currentTimeMillis() - screenOnStartTime)
        }
    }
}
