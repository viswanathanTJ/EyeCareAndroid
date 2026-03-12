package com.viswa2k.eyecare.widget

import android.content.Context
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.glance.Button
import androidx.glance.GlanceId
import androidx.glance.GlanceModifier
import androidx.glance.GlanceTheme
import androidx.glance.action.actionStartActivity
import androidx.glance.appwidget.GlanceAppWidget
import androidx.glance.appwidget.provideContent
import androidx.glance.layout.Alignment
import androidx.glance.layout.Column
import androidx.glance.layout.Row
import androidx.glance.layout.Spacer
import androidx.glance.layout.fillMaxSize
import androidx.glance.layout.height
import androidx.glance.layout.padding
import androidx.glance.layout.width
import androidx.glance.text.FontWeight
import androidx.glance.text.Text
import androidx.glance.text.TextStyle
import com.viswa2k.eyecare.MainActivity

class EyeCareWidget : GlanceAppWidget() {

    override suspend fun provideGlance(context: Context, id: GlanceId) {
        val stateHelper = WidgetStateHelper(context)
        val state = stateHelper.getState()

        provideContent {
            GlanceTheme {
                EyeCareWidgetContent(state = state)
            }
        }
    }
}

@Composable
private fun EyeCareWidgetContent(state: WidgetState) {
    Column(
        modifier = GlanceModifier
            .fillMaxSize()
            .padding(12.dp),
        verticalAlignment = Alignment.CenterVertically,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = "Eye Care",
            style = TextStyle(fontWeight = FontWeight.Bold)
        )

        Spacer(modifier = GlanceModifier.height(8.dp))

        if (state.isMonitoring) {
            Text(
                text = state.timeDisplay,
                style = TextStyle(fontWeight = FontWeight.Bold)
            )

            Spacer(modifier = GlanceModifier.height(4.dp))

            Text(text = "Breaks: ${state.breaksToday}")
        } else {
            Text(text = "Not monitoring")
        }

        Spacer(modifier = GlanceModifier.height(8.dp))

        Button(
            text = if (state.isMonitoring) "Open" else "Start",
            onClick = actionStartActivity<MainActivity>()
        )
    }
}
