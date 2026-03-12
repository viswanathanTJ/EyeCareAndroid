package com.viswa2k.eyecare.di

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.preferencesDataStore
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import com.viswa2k.eyecare.domain.GetDailyStatsUseCase
import com.viswa2k.eyecare.domain.GetStreakUseCase
import com.viswa2k.eyecare.domain.RecordBreakEventUseCase
import com.viswa2k.eyecare.service.MonitoringState
import com.viswa2k.eyecare.service.ServiceRestartHelper
import com.viswa2k.eyecare.service.TimerManager
import com.viswa2k.eyecare.ui.break_.BreakViewModel
import com.viswa2k.eyecare.ui.home.HomeViewModel
import com.viswa2k.eyecare.ui.onboarding.OnboardingViewModel
import com.viswa2k.eyecare.ui.settings.SettingsViewModel
import com.viswa2k.eyecare.ui.stats.StatsViewModel
import org.koin.android.ext.koin.androidContext
import org.koin.core.module.dsl.viewModel
import org.koin.dsl.module

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "eye_care_preferences")

val appModule = module {
    single<DataStore<Preferences>> { androidContext().dataStore }
    single { PreferencesManager(get()) }
    single { MonitoringState() }
    single { TimerManager(get()) }
    single { ServiceRestartHelper(get()) }

    // Use Cases
    factory { RecordBreakEventUseCase(get()) }
    factory { GetDailyStatsUseCase(get()) }
    factory { GetStreakUseCase(get()) }

    // ViewModels
    viewModel { HomeViewModel(get(), get(), get(), androidContext()) }
    viewModel { SettingsViewModel(get()) }
    viewModel { StatsViewModel(get(), get()) }
    viewModel { BreakViewModel(get(), get()) }
    viewModel { OnboardingViewModel(get()) }
}
