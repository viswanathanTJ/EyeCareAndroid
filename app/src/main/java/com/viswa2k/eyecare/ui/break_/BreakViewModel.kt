package com.viswa2k.eyecare.ui.break_

import android.os.CountDownTimer
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import com.viswa2k.eyecare.data.db.entity.BreakEventType
import com.viswa2k.eyecare.data.repository.BreakRepository
import com.viswa2k.eyecare.domain.BreakTipsProvider
import com.viswa2k.eyecare.domain.RecordBreakEventUseCase
import com.viswa2k.eyecare.service.BreakResult
import com.viswa2k.eyecare.service.MonitoringState
import com.viswa2k.eyecare.service.SoundManager
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

data class BreakUiState(
    val remainingSeconds: Int = 20,
    val totalSeconds: Int = 20,
    val tip: String = BreakTipsProvider.getRandomTip(),
    val snoozeDurationMinutes: Int = 5,
    val isFinished: Boolean = false
) {
    val progress: Float get() = if (totalSeconds > 0) remainingSeconds.toFloat() / totalSeconds else 1f
}

class BreakViewModel(
    private val recordBreakEventUseCase: RecordBreakEventUseCase,
    private val preferencesManager: PreferencesManager,
    private val monitoringState: MonitoringState,
    breakRepository: BreakRepository,
    private val soundManager: SoundManager
) : ViewModel() {

    private val _uiState = MutableStateFlow(BreakUiState())
    val uiState: StateFlow<BreakUiState> = _uiState.asStateFlow()

    val breaksToday: StateFlow<Int> = breakRepository.getTodayStats()
        .map { it?.breaksTaken ?: 0 }
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 0)

    private var countDownTimer: CountDownTimer? = null

    init {
        viewModelScope.launch {
            val breakDuration = preferencesManager.breakDurationSeconds.first()
            val snoozeDuration = preferencesManager.snoozeDurationMinutes.first()
            _uiState.value = BreakUiState(
                remainingSeconds = breakDuration,
                totalSeconds = breakDuration,
                snoozeDurationMinutes = snoozeDuration
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
                    soundManager.playBreakComplete()
                }
                monitoringState.emitBreakResult(BreakResult.Taken)
            }
        }.start()
    }

    fun skip() {
        countDownTimer?.cancel()
        viewModelScope.launch {
            recordBreakEventUseCase(BreakEventType.SKIPPED)
        }
        monitoringState.emitBreakResult(BreakResult.Skipped)
    }

    fun snooze() {
        countDownTimer?.cancel()
        viewModelScope.launch {
            recordBreakEventUseCase(BreakEventType.SNOOZED)
        }
        monitoringState.emitBreakResult(BreakResult.Snoozed)
    }

    override fun onCleared() {
        super.onCleared()
        countDownTimer?.cancel()
    }
}
