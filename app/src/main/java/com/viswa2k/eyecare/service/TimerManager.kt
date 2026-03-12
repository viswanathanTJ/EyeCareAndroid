package com.viswa2k.eyecare.service

import android.os.CountDownTimer
import com.viswa2k.eyecare.domain.TimerState

class TimerManager(
    private val monitoringState: MonitoringState
) {
    private var countDownTimer: CountDownTimer? = null
    private var cycleDurationMillis: Long = TimerState.CYCLE_DURATION_MILLIS

    var onCycleComplete: (() -> Unit)? = null

    fun setCycleDuration(minutes: Int) {
        cycleDurationMillis = minutes * 60 * 1000L
    }

    fun start() {
        cancel()
        val currentState = monitoringState.timerState.value
        val startMillis = if (currentState.remainingMillis > 0 && currentState.remainingMillis < cycleDurationMillis) {
            currentState.remainingMillis
        } else {
            cycleDurationMillis
        }

        countDownTimer = object : CountDownTimer(startMillis, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                monitoringState.updateTimerState(
                    monitoringState.timerState.value.copy(
                        remainingMillis = millisUntilFinished,
                        isRunning = true
                    )
                )
            }

            override fun onFinish() {
                val currentCycleCount = monitoringState.timerState.value.cycleCount + 1
                monitoringState.updateTimerState(
                    TimerState(
                        remainingMillis = 0,
                        isRunning = false,
                        cycleCount = currentCycleCount
                    )
                )
                onCycleComplete?.invoke()
            }
        }.start()

        monitoringState.updateTimerState(
            currentState.copy(isRunning = true)
        )
    }

    fun cancel() {
        countDownTimer?.cancel()
        countDownTimer = null
    }

    fun reset() {
        cancel()
        monitoringState.updateTimerState(
            TimerState(
                remainingMillis = cycleDurationMillis,
                isRunning = false,
                cycleCount = monitoringState.timerState.value.cycleCount
            )
        )
    }

    fun resetAndStart() {
        reset()
        start()
    }
}
