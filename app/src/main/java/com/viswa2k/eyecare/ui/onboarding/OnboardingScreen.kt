package com.viswa2k.eyecare.ui.onboarding

import android.Manifest
import android.content.Context
import android.content.Intent
import android.net.Uri
import android.os.PowerManager
import android.provider.Settings
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.BatteryChargingFull
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.RemoveRedEye
import androidx.compose.material.icons.filled.ThumbUp
import androidx.compose.material3.Button
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel

@Composable
fun OnboardingScreen(
    onComplete: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: OnboardingViewModel = koinViewModel()
) {
    val currentStep by viewModel.currentStep.collectAsStateWithLifecycle()
    val context = LocalContext.current

    val notificationPermissionLauncher = rememberLauncherForActivityResult(
        ActivityResultContracts.RequestPermission()
    ) { viewModel.nextStep() }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        when (currentStep) {
            0 -> WelcomeStep()
            1 -> NotificationStep(
                onRequestPermission = {
                    notificationPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
                }
            )
            2 -> BatteryOptimizationStep(
                onRequestExemption = {
                    val pm = context.getSystemService(Context.POWER_SERVICE) as PowerManager
                    if (!pm.isIgnoringBatteryOptimizations(context.packageName)) {
                        val intent = Intent(Settings.ACTION_REQUEST_IGNORE_BATTERY_OPTIMIZATIONS).apply {
                            data = Uri.parse("package:${context.packageName}")
                        }
                        context.startActivity(intent)
                    }
                    viewModel.nextStep()
                }
            )
            3 -> ReadyStep()
        }

        Spacer(modifier = Modifier.height(48.dp))

        // Step indicator
        Row(
            horizontalArrangement = Arrangement.Center,
            modifier = Modifier
                .fillMaxWidth()
                .padding(bottom = 16.dp)
        ) {
            repeat(viewModel.totalSteps) { index ->
                Text(
                    text = if (index == currentStep) "\u25CF" else "\u25CB",
                    color = if (index == currentStep) MaterialTheme.colorScheme.primary
                            else MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(horizontal = 4.dp)
                )
            }
        }

        // Navigation buttons
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            if (currentStep > 0) {
                OutlinedButton(onClick = { viewModel.previousStep() }) {
                    Text("Back")
                }
            } else {
                Spacer(modifier = Modifier.weight(1f))
            }

            when (currentStep) {
                viewModel.totalSteps - 1 -> {
                    Button(onClick = {
                        viewModel.completeOnboarding()
                        onComplete()
                    }) {
                        Text("Get Started")
                    }
                }
                1 -> {
                    // Notification step: no Next — must tap "Allow Notifications" above
                    Spacer(modifier = Modifier.weight(1f))
                }
                else -> {
                    Button(onClick = { viewModel.nextStep() }) {
                        Text("Next")
                    }
                }
            }
        }

        // Only battery optimization step is skippable
        if (currentStep == 2) {
            Spacer(modifier = Modifier.height(8.dp))
            TextButton(onClick = { viewModel.nextStep() }) {
                Text("Skip")
            }
        }
    }
}

@Composable
private fun StepHeader(icon: ImageVector, title: String, description: String) {
    Icon(
        imageVector = icon,
        contentDescription = null,
        tint = MaterialTheme.colorScheme.primary,
        modifier = Modifier.size(64.dp)
    )
    Spacer(modifier = Modifier.height(24.dp))
    Text(
        text = title,
        style = MaterialTheme.typography.headlineMedium,
        textAlign = TextAlign.Center
    )
    Spacer(modifier = Modifier.height(12.dp))
    Text(
        text = description,
        style = MaterialTheme.typography.bodyLarge,
        textAlign = TextAlign.Center,
        color = MaterialTheme.colorScheme.onSurfaceVariant
    )
}

@Composable
private fun WelcomeStep() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        StepHeader(
            icon = Icons.Default.RemoveRedEye,
            title = "Welcome to Eye Care",
            description = "Protect your eyes with the 20-20-20 rule:\nEvery 20 minutes, look at something 20 feet away for 20 seconds."
        )
    }
}

@Composable
private fun NotificationStep(onRequestPermission: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        StepHeader(
            icon = Icons.Default.Notifications,
            title = "Stay Notified",
            description = "We need notification permission to remind you about eye breaks and show monitoring status."
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRequestPermission) {
            Text("Allow Notifications")
        }
    }
}

@Composable
private fun BatteryOptimizationStep(onRequestExemption: () -> Unit) {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        StepHeader(
            icon = Icons.Default.BatteryChargingFull,
            title = "Reliable Monitoring",
            description = "To ensure the timer runs reliably in the background, please exempt Eye Care from battery optimization. This prevents Android from stopping the monitoring service."
        )
        Spacer(modifier = Modifier.height(24.dp))
        Button(onClick = onRequestExemption) {
            Text("Disable Battery Optimization")
        }
    }
}

@Composable
private fun ReadyStep() {
    Column(horizontalAlignment = Alignment.CenterHorizontally) {
        StepHeader(
            icon = Icons.Default.ThumbUp,
            title = "You're All Set!",
            description = "Eye Care will start monitoring automatically. You'll get reminders to take eye breaks. Customize settings anytime from the Settings tab."
        )
    }
}
