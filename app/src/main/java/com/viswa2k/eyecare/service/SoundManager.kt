package com.viswa2k.eyecare.service

import android.content.Context
import android.media.MediaPlayer
import android.media.RingtoneManager
import android.os.VibrationEffect
import android.os.Vibrator
import android.os.VibratorManager
import com.viswa2k.eyecare.data.datastore.PreferencesManager
import kotlinx.coroutines.flow.first

class SoundManager(
    private val context: Context,
    private val preferencesManager: PreferencesManager
) {
    private var mediaPlayer: MediaPlayer? = null

    private val vibrator: Vibrator by lazy {
        if (android.os.Build.VERSION.SDK_INT >= 31) {
            val vm = context.getSystemService(Context.VIBRATOR_MANAGER_SERVICE) as VibratorManager
            vm.defaultVibrator
        } else {
            @Suppress("DEPRECATION")
            context.getSystemService(Context.VIBRATOR_SERVICE) as Vibrator
        }
    }

    suspend fun playBreakReminder() {
        playFeedback(longArrayOf(0, 200, 100, 200))
    }

    suspend fun playBreakComplete() {
        playFeedback(longArrayOf(0, 100, 50, 100, 50, 200))
    }

    private suspend fun playFeedback(vibrationPattern: LongArray) {
        val prefs = preferencesManager.preferencesSnapshot()
        if (prefs.first) playNotificationSound()
        if (prefs.second) vibrate(vibrationPattern)
    }

    private fun playNotificationSound() {
        try {
            release()
            val uri = RingtoneManager.getDefaultUri(RingtoneManager.TYPE_NOTIFICATION)
            mediaPlayer = MediaPlayer.create(context, uri)?.apply {
                setOnCompletionListener { it.release() }
                start()
            }
        } catch (_: Exception) { }
    }

    private fun vibrate(pattern: LongArray) {
        try {
            vibrator.vibrate(VibrationEffect.createWaveform(pattern, -1))
        } catch (_: Exception) { }
    }

    fun release() {
        mediaPlayer?.release()
        mediaPlayer = null
    }
}
