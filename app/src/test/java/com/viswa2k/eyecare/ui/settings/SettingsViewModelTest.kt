package com.viswa2k.eyecare.ui.settings

import com.viswa2k.eyecare.data.datastore.BreakReminderMode
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.test.StandardTestDispatcher
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.test.setMain
import org.junit.After
import org.junit.Before
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class SettingsViewModelTest {

    private lateinit var preferencesManager: PreferencesManager
    private lateinit var viewModel: SettingsViewModel
    private val testDispatcher = StandardTestDispatcher()

    @Before
    fun setup() {
        Dispatchers.setMain(testDispatcher)
        preferencesManager = mockk(relaxed = true)
        every { preferencesManager.breakReminderMode } returns flowOf(BreakReminderMode.FULL_SCREEN)
        every { preferencesManager.cycleDurationMinutes } returns flowOf(20)
        every { preferencesManager.breakDurationSeconds } returns flowOf(20)
        every { preferencesManager.startOnBoot } returns flowOf(false)
        every { preferencesManager.soundEnabled } returns flowOf(true)
        every { preferencesManager.vibrationEnabled } returns flowOf(true)
        viewModel = SettingsViewModel(preferencesManager)
    }

    @After
    fun tearDown() {
        Dispatchers.resetMain()
    }

    @Test
    fun `setBreakReminderMode delegates to preferences`() = runTest {
        viewModel.setBreakReminderMode(BreakReminderMode.NOTIFICATION)
        advanceUntilIdle()
        coVerify { preferencesManager.setBreakReminderMode(BreakReminderMode.NOTIFICATION) }
    }

    @Test
    fun `setCycleDurationMinutes delegates to preferences`() = runTest {
        viewModel.setCycleDurationMinutes(15)
        advanceUntilIdle()
        coVerify { preferencesManager.setCycleDurationMinutes(15) }
    }

    @Test
    fun `setStartOnBoot delegates to preferences`() = runTest {
        viewModel.setStartOnBoot(true)
        advanceUntilIdle()
        coVerify { preferencesManager.setStartOnBoot(true) }
    }

    @Test
    fun `setSoundEnabled delegates to preferences`() = runTest {
        viewModel.setSoundEnabled(false)
        advanceUntilIdle()
        coVerify { preferencesManager.setSoundEnabled(false) }
    }
}
