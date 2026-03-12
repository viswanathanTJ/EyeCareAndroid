package com.viswa2k.eyecare.ui.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.viswa2k.eyecare.data.datastore.BreakReminderMode

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val breakReminderMode by viewModel.breakReminderMode.collectAsStateWithLifecycle()
    val cycleDuration by viewModel.cycleDurationMinutes.collectAsStateWithLifecycle()
    val breakDuration by viewModel.breakDurationSeconds.collectAsStateWithLifecycle()
    val startOnBoot by viewModel.startOnBoot.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()

    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
    ) {
        Text(
            text = "Settings",
            style = MaterialTheme.typography.headlineLarge
        )

        Spacer(modifier = Modifier.height(24.dp))

        // Timer Settings
        SectionHeader("Timer")

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cycle Duration: $cycleDuration min",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = cycleDuration.toFloat(),
                    onValueChange = { viewModel.setCycleDurationMinutes(it.toInt()) },
                    valueRange = 5f..60f,
                    steps = 10
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Break Duration: $breakDuration sec",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = breakDuration.toFloat(),
                    onValueChange = { viewModel.setBreakDurationSeconds(it.toInt()) },
                    valueRange = 10f..60f,
                    steps = 9
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Break Reminder Mode
        SectionHeader("Break Reminder")

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                var expanded by remember { mutableStateOf(false) }
                ExposedDropdownMenuBox(
                    expanded = expanded,
                    onExpandedChange = { expanded = !expanded }
                ) {
                    TextField(
                        value = breakReminderMode.displayName(),
                        onValueChange = {},
                        readOnly = true,
                        trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded = expanded) },
                        modifier = Modifier
                            .menuAnchor(MenuAnchorType.PrimaryNotEditable)
                            .fillMaxWidth()
                    )
                    ExposedDropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false }
                    ) {
                        BreakReminderMode.entries.forEach { mode ->
                            DropdownMenuItem(
                                text = { Text(mode.displayName()) },
                                onClick = {
                                    viewModel.setBreakReminderMode(mode)
                                    expanded = false
                                }
                            )
                        }
                    }
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // General Settings
        SectionHeader("General")

        Card(
            modifier = Modifier.fillMaxWidth()
        ) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsToggle(
                    title = "Start on boot",
                    checked = startOnBoot,
                    onCheckedChange = viewModel::setStartOnBoot
                )

                SettingsToggle(
                    title = "Sound",
                    checked = soundEnabled,
                    onCheckedChange = viewModel::setSoundEnabled
                )

                SettingsToggle(
                    title = "Vibration",
                    checked = vibrationEnabled,
                    onCheckedChange = viewModel::setVibrationEnabled
                )
            }
        }
    }
}

@Composable
private fun SectionHeader(title: String) {
    Text(
        text = title,
        style = MaterialTheme.typography.titleMedium,
        color = MaterialTheme.colorScheme.primary,
        modifier = Modifier.padding(bottom = 8.dp)
    )
}

@Composable
private fun SettingsToggle(
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(vertical = 8.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f)
        )
        Switch(
            checked = checked,
            onCheckedChange = onCheckedChange
        )
    }
}

private fun BreakReminderMode.displayName(): String = when (this) {
    BreakReminderMode.FULL_SCREEN -> "Full Screen"
    BreakReminderMode.OVERLAY_POPUP -> "Overlay Popup"
    BreakReminderMode.NOTIFICATION -> "Notification"
    BreakReminderMode.SILENT -> "Silent"
}
