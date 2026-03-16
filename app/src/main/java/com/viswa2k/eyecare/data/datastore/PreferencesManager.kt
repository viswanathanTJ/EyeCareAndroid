package com.viswa2k.eyecare.data.datastore

import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.intPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map

class PreferencesManager(
    private val dataStore: DataStore<Preferences>
) {
    val isMonitoringEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.MONITORING_ENABLED] ?: false
    }

    val breakReminderMode: Flow<BreakReminderMode> = dataStore.data.map { prefs ->
        val name = prefs[Keys.BREAK_REMINDER_MODE] ?: BreakReminderMode.FULL_SCREEN.name
        BreakReminderMode.valueOf(name)
    }

    val cycleDurationMinutes: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.CYCLE_DURATION_MINUTES] ?: 20
    }

    val breakDurationSeconds: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.BREAK_DURATION_SECONDS] ?: 20
    }

    val snoozeDurationMinutes: Flow<Int> = dataStore.data.map { prefs ->
        prefs[Keys.SNOOZE_DURATION_MINUTES] ?: 5
    }

    val startOnBoot: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.START_ON_BOOT] ?: false
    }

    val soundEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.SOUND_ENABLED] ?: true
    }

    val vibrationEnabled: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.VIBRATION_ENABLED] ?: true
    }

    val accentColor: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.ACCENT_COLOR] ?: "2E7D6F"
    }

    val secondaryAccentColor: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.SECONDARY_ACCENT_COLOR] ?: "2E7D32"
    }

    val themeMode: Flow<String> = dataStore.data.map { prefs ->
        prefs[Keys.THEME_MODE] ?: "SYSTEM"
    }

    val onboardingCompleted: Flow<Boolean> = dataStore.data.map { prefs ->
        prefs[Keys.ONBOARDING_COMPLETED] ?: false
    }

    suspend fun setMonitoringEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.MONITORING_ENABLED] = enabled }
    }

    suspend fun setBreakReminderMode(mode: BreakReminderMode) {
        dataStore.edit { it[Keys.BREAK_REMINDER_MODE] = mode.name }
    }

    suspend fun setCycleDurationMinutes(minutes: Int) {
        dataStore.edit { it[Keys.CYCLE_DURATION_MINUTES] = minutes }
    }

    suspend fun setBreakDurationSeconds(seconds: Int) {
        dataStore.edit { it[Keys.BREAK_DURATION_SECONDS] = seconds }
    }

    suspend fun setSnoozeDurationMinutes(minutes: Int) {
        dataStore.edit { it[Keys.SNOOZE_DURATION_MINUTES] = minutes }
    }

    suspend fun setStartOnBoot(enabled: Boolean) {
        dataStore.edit { it[Keys.START_ON_BOOT] = enabled }
    }

    suspend fun setSoundEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.SOUND_ENABLED] = enabled }
    }

    suspend fun setVibrationEnabled(enabled: Boolean) {
        dataStore.edit { it[Keys.VIBRATION_ENABLED] = enabled }
    }

    /** Returns (soundEnabled, vibrationEnabled) in a single DataStore read. */
    suspend fun preferencesSnapshot(): Pair<Boolean, Boolean> {
        val prefs = dataStore.data.first()
        return Pair(
            prefs[Keys.SOUND_ENABLED] ?: true,
            prefs[Keys.VIBRATION_ENABLED] ?: true
        )
    }

    suspend fun setAccentColor(name: String) {
        dataStore.edit { it[Keys.ACCENT_COLOR] = name }
    }

    suspend fun setSecondaryAccentColor(name: String) {
        dataStore.edit { it[Keys.SECONDARY_ACCENT_COLOR] = name }
    }

    suspend fun setThemeMode(mode: String) {
        dataStore.edit { it[Keys.THEME_MODE] = mode }
    }

    suspend fun setOnboardingCompleted(completed: Boolean) {
        dataStore.edit { it[Keys.ONBOARDING_COMPLETED] = completed }
    }

    private object Keys {
        val MONITORING_ENABLED = booleanPreferencesKey("monitoring_enabled")
        val BREAK_REMINDER_MODE = stringPreferencesKey("break_reminder_mode")
        val CYCLE_DURATION_MINUTES = intPreferencesKey("cycle_duration_minutes")
        val BREAK_DURATION_SECONDS = intPreferencesKey("break_duration_seconds")
        val SNOOZE_DURATION_MINUTES = intPreferencesKey("snooze_duration_minutes")
        val START_ON_BOOT = booleanPreferencesKey("start_on_boot")
        val SOUND_ENABLED = booleanPreferencesKey("sound_enabled")
        val VIBRATION_ENABLED = booleanPreferencesKey("vibration_enabled")
        val ACCENT_COLOR = stringPreferencesKey("accent_color")
        val SECONDARY_ACCENT_COLOR = stringPreferencesKey("secondary_accent_color")
        val THEME_MODE = stringPreferencesKey("theme_mode")
        val ONBOARDING_COMPLETED = booleanPreferencesKey("onboarding_completed")
    }
}
