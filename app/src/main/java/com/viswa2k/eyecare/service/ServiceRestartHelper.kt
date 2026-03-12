package com.viswa2k.eyecare.service

import android.content.Context
import android.content.Intent
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import kotlinx.coroutines.flow.first

class ServiceRestartHelper(
    private val preferencesManager: PreferencesManager
) {
    suspend fun shouldRestart(): Boolean {
        return preferencesManager.isMonitoringEnabled.first()
    }

    fun restartService(context: Context) {
        val intent = Intent(context, EyeCareService::class.java).apply {
            action = EyeCareService.ACTION_START
        }
        context.startForegroundService(intent)
    }
}
