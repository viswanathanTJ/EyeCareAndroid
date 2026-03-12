package com.viswa2k.eyecare.ui.home

import android.Manifest
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.content.ContextCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import com.viswa2k.eyecare.data.db.entity.DailyStats
import com.viswa2k.eyecare.data.repository.BreakRepository
import com.viswa2k.eyecare.domain.TimerState
import com.viswa2k.eyecare.service.EyeCareService
import com.viswa2k.eyecare.service.MonitoringState
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class HomeViewModel(
    private val monitoringState: MonitoringState,
    private val preferencesManager: PreferencesManager,
    private val breakRepository: BreakRepository,
    private val context: Context
) : ViewModel() {

    val timerState: StateFlow<TimerState> = monitoringState.timerState

    val isMonitoring: StateFlow<Boolean> = monitoringState.isMonitoring

    val screenOnTimeToday: StateFlow<Long> = monitoringState.screenOnTimeToday

    val todayStats: StateFlow<DailyStats?> = breakRepository.getTodayStats()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    val currentStreak: StateFlow<Int?> = breakRepository.getCurrentStreak()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), null)

    init {
        // Start screen time tracking immediately (even before service starts)
        if (monitoringState.screenOnTimeToday.value == 0L) {
            monitoringState.onScreenOn()
            viewModelScope.launch {
                while (true) {
                    kotlinx.coroutines.delay(1000L)
                    monitoringState.tickScreenTime()
                }
            }
        }

        // Auto-start monitoring when app opens
        viewModelScope.launch {
            if (!monitoringState.isMonitoring.value && hasNotificationPermission()) {
                startService()
            }
        }
    }

    private fun hasNotificationPermission(): Boolean {
        return ContextCompat.checkSelfPermission(
            context, Manifest.permission.POST_NOTIFICATIONS
        ) == PackageManager.PERMISSION_GRANTED
    }

    fun toggleMonitoring() {
        val currentlyMonitoring = isMonitoring.value
        viewModelScope.launch {
            preferencesManager.setMonitoringEnabled(!currentlyMonitoring)
        }
        if (currentlyMonitoring) {
            stopService()
        } else {
            startService()
        }
    }

    private fun startService() {
        if (!hasNotificationPermission()) return
        viewModelScope.launch {
            preferencesManager.setMonitoringEnabled(true)
        }
        try {
            val intent = Intent(context, EyeCareService::class.java).apply {
                action = EyeCareService.ACTION_START
            }
            context.startForegroundService(intent)
        } catch (e: Exception) {
            // Service failed to start — don't crash the app
            monitoringState.setMonitoring(false)
        }
    }

    private fun stopService() {
        val intent = Intent(context, EyeCareService::class.java).apply {
            action = EyeCareService.ACTION_STOP
        }
        context.stopService(intent)
    }
}
