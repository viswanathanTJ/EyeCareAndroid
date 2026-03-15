package com.viswa2k.eyecare.service

import android.app.Service
import android.content.Intent
import android.content.IntentFilter
import android.content.pm.ServiceInfo
import android.os.IBinder
import com.viswa2k.eyecare.data.datastore.BreakReminderMode
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import com.viswa2k.eyecare.receiver.ScreenStateReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.android.ext.android.inject

class EyeCareService : Service() {

    private val timerManager: TimerManager by inject()
    private val monitoringState: MonitoringState by inject()
    private val preferencesManager: PreferencesManager by inject()
    private val soundManager: SoundManager by inject()

    private lateinit var notificationHelper: NotificationHelper
    private var screenStateReceiver: ScreenStateReceiver? = null
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    override fun onCreate() {
        super.onCreate()
        notificationHelper = NotificationHelper(this)

        timerManager.onCycleComplete = {
            serviceScope.launch {
                onCycleComplete()
            }
        }

        registerScreenReceiver()
        observeTimerState()
        observeBreakResult()
        startScreenTimeTracker()
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        when (intent?.action) {
            ACTION_START -> startMonitoring()
            ACTION_STOP -> stopMonitoring()
            null -> {
                // Restarted by system after process death (START_STICKY)
                if (!monitoringState.isMonitoring.value) {
                    startMonitoring()
                }
            }
        }
        return START_STICKY
    }

    override fun onBind(intent: Intent?): IBinder? = null

    override fun onDestroy() {
        super.onDestroy()
        unregisterScreenReceiver()
        timerManager.cancel()
        monitoringState.setMonitoring(false)
        serviceScope.cancel()
    }

    private fun startScreenTimeTracker() {
        monitoringState.onScreenOn()
        serviceScope.launch {
            while (true) {
                kotlinx.coroutines.delay(1000L)
                monitoringState.tickScreenTime()
            }
        }
    }

    private fun startMonitoring() {
        monitoringState.setMonitoring(true)

        val notification = notificationHelper.buildMonitoringNotification(20, 0)
        startForeground(
            NotificationHelper.MONITORING_NOTIFICATION_ID,
            notification,
            ServiceInfo.FOREGROUND_SERVICE_TYPE_SPECIAL_USE
        )

        serviceScope.launch {
            val cycleDuration = preferencesManager.cycleDurationMinutes.first()
            timerManager.setCycleDuration(cycleDuration)
            timerManager.resetAndStart()
        }
    }

    private fun stopMonitoring() {
        timerManager.cancel()
        monitoringState.setMonitoring(false)
        monitoringState.resetTimer()
        stopForeground(STOP_FOREGROUND_REMOVE)
        stopSelf()
    }

    private fun registerScreenReceiver() {
        screenStateReceiver = ScreenStateReceiver(
            onScreenOn = {
                monitoringState.onScreenOn()
                if (monitoringState.isMonitoring.value) {
                    timerManager.resetAndStart()
                }
            },
            onScreenOff = {
                monitoringState.onScreenOff()
                timerManager.reset()
            }
        )
        val filter = IntentFilter().apply {
            addAction(Intent.ACTION_SCREEN_ON)
            addAction(Intent.ACTION_SCREEN_OFF)
        }
        registerReceiver(screenStateReceiver, filter, RECEIVER_EXPORTED)
    }

    private fun unregisterScreenReceiver() {
        screenStateReceiver?.let {
            unregisterReceiver(it)
            screenStateReceiver = null
        }
    }

    private fun observeTimerState() {
        serviceScope.launch {
            monitoringState.timerState.collect { state ->
                if (monitoringState.isMonitoring.value && state.isRunning) {
                    notificationHelper.updateMonitoringNotification(
                        state.remainingMinutes,
                        state.remainingSecondsInMinute
                    )
                }
            }
        }
    }

    private fun observeBreakResult() {
        serviceScope.launch {
            monitoringState.breakResult.collect { result ->
                when (result) {
                    is BreakResult.Taken, is BreakResult.Skipped -> {
                        // Restart full cycle timer
                        val cycleDuration = preferencesManager.cycleDurationMinutes.first()
                        timerManager.setCycleDuration(cycleDuration)
                        timerManager.resetAndStart()
                    }
                    is BreakResult.Snoozed -> {
                        // Restart with snooze duration
                        val snoozeDuration = preferencesManager.snoozeDurationMinutes.first()
                        timerManager.startSnooze(snoozeDuration)
                    }
                }
            }
        }
    }

    private suspend fun onCycleComplete() {
        // Play break reminder sound
        soundManager.playBreakReminder()
        // Timer stays stopped — waiting for user to take/skip/snooze break
        val mode = preferencesManager.breakReminderMode.first()
        when (mode) {
            BreakReminderMode.NOTIFICATION -> {
                notificationHelper.showBreakNotification()
            }
            BreakReminderMode.SILENT -> {
                // Silent mode: auto-restart since there's no user interaction
                timerManager.resetAndStart()
            }
            BreakReminderMode.FULL_SCREEN -> {
                val breakIntent = Intent(this, com.viswa2k.eyecare.ui.break_.BreakActivity::class.java).apply {
                    addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                }
                startActivity(breakIntent)
            }
            BreakReminderMode.OVERLAY_POPUP -> {
                val overlayIntent = Intent(this, com.viswa2k.eyecare.ui.break_.BreakOverlayService::class.java)
                startService(overlayIntent)
            }
        }
    }

    companion object {
        const val ACTION_START = "com.viswa2k.eyecare.START_MONITORING"
        const val ACTION_STOP = "com.viswa2k.eyecare.STOP_MONITORING"
    }
}
