package com.viswa2k.eyecare.ui.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.viswa2k.eyecare.data.datastore.BreakReminderMode
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class SettingsViewModel(
    private val preferencesManager: PreferencesManager
) : ViewModel() {

    val breakReminderMode: StateFlow<BreakReminderMode> = preferencesManager.breakReminderMode
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), BreakReminderMode.FULL_SCREEN)

    val cycleDurationMinutes: StateFlow<Int> = preferencesManager.cycleDurationMinutes
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 20)

    val breakDurationSeconds: StateFlow<Int> = preferencesManager.breakDurationSeconds
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), 20)

    val startOnBoot: StateFlow<Boolean> = preferencesManager.startOnBoot
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), false)

    val soundEnabled: StateFlow<Boolean> = preferencesManager.soundEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    val vibrationEnabled: StateFlow<Boolean> = preferencesManager.vibrationEnabled
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), true)

    fun setBreakReminderMode(mode: BreakReminderMode) {
        viewModelScope.launch { preferencesManager.setBreakReminderMode(mode) }
    }

    fun setCycleDurationMinutes(minutes: Int) {
        viewModelScope.launch { preferencesManager.setCycleDurationMinutes(minutes) }
    }

    fun setBreakDurationSeconds(seconds: Int) {
        viewModelScope.launch { preferencesManager.setBreakDurationSeconds(seconds) }
    }

    fun setStartOnBoot(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setStartOnBoot(enabled) }
    }

    fun setSoundEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setSoundEnabled(enabled) }
    }

    fun setVibrationEnabled(enabled: Boolean) {
        viewModelScope.launch { preferencesManager.setVibrationEnabled(enabled) }
    }
}
