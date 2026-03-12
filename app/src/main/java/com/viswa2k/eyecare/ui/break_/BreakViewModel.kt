package com.viswa2k.eyecare.ui.break_

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import com.viswa2k.eyecare.data.db.entity.BreakEventType
import com.viswa2k.eyecare.domain.BreakTipsProvider
import com.viswa2k.eyecare.domain.RecordBreakEventUseCase
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch

data class BreakUiState(
    val remainingSeconds: Int = 20,
    val totalSeconds: Int = 20,
    val tip: String = BreakTipsProvider.getRandomTip(),
    val isFinished: Boolean = false
) {
    val progress: Float get() = remainingSeconds.toFloat() / totalSeconds
}

class BreakViewModel(
    private val recordBreakEventUseCase: RecordBreakEventUseCase,
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BreakUiState())
    val uiState: StateFlow<BreakUiState> = _uiState.asStateFlow()

    private var countDownTimer: CountDownTimer? = null

    init {
        viewModelScope.launch {
            val breakDuration = preferencesManager.breakDurationSeconds.first()
            _uiState.value = BreakUiState(
                remainingSeconds = breakDuration,
                totalSeconds = breakDuration
            )
            startCountdown(breakDuration)
        }
    }

    private fun startCountdown(seconds: Int) {
        countDownTimer = object : CountDownTimer(seconds * 1000L, 1000L) {
            override fun onTick(millisUntilFinished: Long) {
                _uiState.value = _uiState.value.copy(
                    remainingSeconds = (millisUntilFinished / 1000).toInt()
                )
            }

            override fun onFinish() {
                _uiState.value = _uiState.value.copy(
                    remainingSeconds = 0,
                    isFinished = true
                )
                viewModelScope.launch {
                    recordBreakEventUseCase(BreakEventType.TAKEN)
                }
            }
        }.start()
    }

    fun skip() {
        countDownTimer?.cancel()
        viewModelScope.launch {
            recordBreakEventUseCase(BreakEventType.SKIPPED)
        }
    }

    fun snooze() {
        countDownTimer?.cancel()
        viewModelScope.launch {
            recordBreakEventUseCase(BreakEventType.SNOOZED)
        }
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
