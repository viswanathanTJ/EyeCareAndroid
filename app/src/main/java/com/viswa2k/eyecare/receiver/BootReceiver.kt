package com.viswa2k.eyecare.receiver

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import com.viswa2k.eyecare.service.EyeCareService
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import org.koin.core.component.KoinComponent
import org.koin.core.component.inject

class BootReceiver : BroadcastReceiver(), KoinComponent {

    private val preferencesManager: PreferencesManager by inject()

    override fun onReceive(context: Context, intent: Intent) {
        if (intent.action == Intent.ACTION_BOOT_COMPLETED) {
            CoroutineScope(Dispatchers.Main).launch {
                val startOnBoot = preferencesManager.startOnBoot.first()
                val wasMonitoring = preferencesManager.isMonitoringEnabled.first()
                if (startOnBoot && wasMonitoring) {
                    val serviceIntent = Intent(context, EyeCareService::class.java).apply {
                        action = EyeCareService.ACTION_START
                    }
                    context.startForegroundService(serviceIntent)
                }
            }
        }
    }
}
