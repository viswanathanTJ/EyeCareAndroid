package com.viswa2k.eyecare.ui.break_

import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import com.viswa2k.eyecare.ui.theme.EyeCareTheme

class BreakActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Show over lock screen
        setShowWhenLocked(true)
        setTurnScreenOn(true)
        window.addFlags(WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON)

        setContent {
            EyeCareTheme {
                BreakScreen(
                    onDismiss = { finish() }
                )
            }
        }
    }
}
