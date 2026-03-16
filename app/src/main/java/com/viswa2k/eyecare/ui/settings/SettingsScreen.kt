package com.viswa2k.eyecare.ui.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Check
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.ExposedDropdownMenuBox
import androidx.compose.material3.ExposedDropdownMenuDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.MenuAnchorType
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TextField
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalUriHandler
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.text.withStyle
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.input.KeyboardCapitalization
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.viswa2k.eyecare.data.datastore.BreakReminderMode
import com.viswa2k.eyecare.ui.theme.AccentColor
import com.viswa2k.eyecare.ui.theme.ThemeMode
import com.viswa2k.eyecare.ui.theme.colorToHex
import com.viswa2k.eyecare.ui.theme.hexToColor
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = koinViewModel()
) {
    val accentColorName by viewModel.accentColor.collectAsStateWithLifecycle()
    val secondaryColorName by viewModel.secondaryAccentColor.collectAsStateWithLifecycle()
    val themeModeName by viewModel.themeMode.collectAsStateWithLifecycle()
    val breakReminderMode by viewModel.breakReminderMode.collectAsStateWithLifecycle()
    val cycleDuration by viewModel.cycleDurationMinutes.collectAsStateWithLifecycle()
    val breakDuration by viewModel.breakDurationSeconds.collectAsStateWithLifecycle()
    val snoozeDuration by viewModel.snoozeDurationMinutes.collectAsStateWithLifecycle()
    var localCycleDuration by remember { mutableStateOf(cycleDuration) }
    var localBreakDuration by remember { mutableStateOf(breakDuration) }
    var localSnoozeDuration by remember { mutableStateOf(snoozeDuration) }
    val startOnBoot by viewModel.startOnBoot.collectAsStateWithLifecycle()
    val soundEnabled by viewModel.soundEnabled.collectAsStateWithLifecycle()
    val vibrationEnabled by viewModel.vibrationEnabled.collectAsStateWithLifecycle()

    var showPrimaryPicker by remember { mutableStateOf(false) }
    var showSecondaryPicker by remember { mutableStateOf(false) }

    // Color picker dialogs
    if (showPrimaryPicker) {
        ColorPickerDialog(
            title = "Primary Color",
            selectedHex = accentColorName,
            onSelect = { viewModel.setAccentColor(it) },
            onDismiss = { showPrimaryPicker = false }
        )
    }
    if (showSecondaryPicker) {
        ColorPickerDialog(
            title = "Secondary Color",
            selectedHex = secondaryColorName,
            onSelect = { viewModel.setSecondaryAccentColor(it) },
            onDismiss = { showSecondaryPicker = false }
        )
    }

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

        // Theme section — mode + colors
        SectionHeader("Theme")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                // Mode chips
                Text(
                    text = "Mode",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                    ThemeMode.entries.forEach { mode ->
                        val isSelected = mode.name == themeModeName
                        FilterChip(
                            selected = isSelected,
                            onClick = { viewModel.setThemeMode(mode.name) },
                            label = { Text(mode.displayName) },
                            leadingIcon = if (isSelected) {
                                { Icon(Icons.Default.Check, null, Modifier.size(16.dp)) }
                            } else null,
                            colors = FilterChipDefaults.filterChipColors(
                                selectedContainerColor = MaterialTheme.colorScheme.primaryContainer,
                                selectedLabelColor = MaterialTheme.colorScheme.onPrimaryContainer
                            )
                        )
                    }
                }

                Spacer(modifier = Modifier.height(20.dp))

                // Color selectors row
                Text(
                    text = "Colors",
                    style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                Spacer(modifier = Modifier.height(8.dp))
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    // Primary
                    ColorSwatch(
                        label = "Primary",
                        hex = accentColorName,
                        onClick = { showPrimaryPicker = true }
                    )

                    Spacer(modifier = Modifier.width(24.dp))

                    // Secondary
                    ColorSwatch(
                        label = "Secondary",
                        hex = secondaryColorName,
                        onClick = { showSecondaryPicker = true }
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Timer Settings
        SectionHeader("Timer")

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                Text(
                    text = "Cycle Duration: ${localCycleDuration} min",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = localCycleDuration.toFloat(),
                    onValueChange = { localCycleDuration = it.toInt() },
                    onValueChangeFinished = { viewModel.setCycleDurationMinutes(localCycleDuration) },
                    valueRange = 5f..60f,
                    steps = 10
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Break Duration: ${localBreakDuration} sec",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = localBreakDuration.toFloat(),
                    onValueChange = { localBreakDuration = it.toInt() },
                    onValueChangeFinished = { viewModel.setBreakDurationSeconds(localBreakDuration) },
                    valueRange = 10f..60f,
                    steps = 9
                )

                Spacer(modifier = Modifier.height(16.dp))

                Text(
                    text = "Snooze Duration: ${localSnoozeDuration} min",
                    style = MaterialTheme.typography.bodyLarge
                )
                Slider(
                    value = localSnoozeDuration.toFloat(),
                    onValueChange = { localSnoozeDuration = it.toInt() },
                    onValueChangeFinished = { viewModel.setSnoozeDurationMinutes(localSnoozeDuration) },
                    valueRange = 1f..15f,
                    steps = 13
                )
            }
        }

        Spacer(modifier = Modifier.height(24.dp))

        // Break Reminder Mode
        SectionHeader("Break Reminder")

        Card(modifier = Modifier.fillMaxWidth()) {
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

        Card(modifier = Modifier.fillMaxWidth()) {
            Column(modifier = Modifier.padding(16.dp)) {
                SettingsToggle("Start on boot", startOnBoot, viewModel::setStartOnBoot)
                SettingsToggle("Sound", soundEnabled, viewModel::setSoundEnabled)
                SettingsToggle("Vibration", vibrationEnabled, viewModel::setVibrationEnabled)
            }
        }

        Spacer(modifier = Modifier.height(32.dp))

        // About
        Text(
            text = "A B O U T",
            style = MaterialTheme.typography.labelLarge.copy(letterSpacing = 4.sp),
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        HorizontalDivider(color = MaterialTheme.colorScheme.outlineVariant)
        Spacer(modifier = Modifier.height(16.dp))

        Text(
            text = "Eye2020 v0.1.0",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "20-20-20 Rule for Eye Care",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Built with Jetpack Compose",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(4.dp))
        Text(
            text = "Created with \uD83D\uDC9B by viswa2k",
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
        Spacer(modifier = Modifier.height(8.dp))

        val uriHandler = LocalUriHandler.current
        Text(
            text = "GitHub",
            style = MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.primary,
                textDecoration = TextDecoration.Underline
            ),
            modifier = Modifier.clickable {
                uriHandler.openUri("https://github.com/viswanathanTJ/EyeCareAndroid")
            }
        )

        Spacer(modifier = Modifier.height(24.dp))
    }
}

@Composable
private fun ColorSwatch(
    label: String,
    hex: String,
    onClick: () -> Unit
) {
    val color = hexToColor(hex) ?: AccentColor.TEAL.lightPrimary
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier
            .clip(RoundedCornerShape(12.dp))
            .clickable(onClick = onClick)
            .padding(8.dp)
    ) {
        Box(
            modifier = Modifier
                .size(36.dp)
                .clip(CircleShape)
                .background(color)
                .border(2.dp, MaterialTheme.colorScheme.onBackground.copy(alpha = 0.2f), CircleShape)
        )
        Spacer(modifier = Modifier.width(10.dp))
        Column {
            Text(
                text = label,
                style = MaterialTheme.typography.labelMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
            Text(
                text = "#$hex",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

// Palette: presets + extended colors
private val paletteColors = listOf(
    Color(0xFFF44336), Color(0xFFE91E63), Color(0xFFC2185B),
    Color(0xFF9C27B0), Color(0xFF7B1FA2), Color(0xFF673AB7),
    Color(0xFF3F51B5), Color(0xFF1565C0), Color(0xFF0288D1),
    Color(0xFF0097A7), Color(0xFF2E7D6F), Color(0xFF009688),
    Color(0xFF2E7D32), Color(0xFF558B2F), Color(0xFF827717),
    Color(0xFFF9A825), Color(0xFFE65100), Color(0xFF5D4037)
)

// Hue spectrum colors for the hue bar
private val hueColors = (0..360 step 30).map { hue ->
    Color.hsv(hue.toFloat(), 0.8f, 0.85f)
}

@Composable
private fun ColorPickerDialog(
    title: String,
    selectedHex: String,
    onSelect: (String) -> Unit,
    onDismiss: () -> Unit
) {
    val currentColor = hexToColor(selectedHex) ?: AccentColor.TEAL.lightPrimary
    var pickedColor by remember { mutableStateOf(currentColor) }
    var hexInput by remember { mutableStateOf(selectedHex) }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text(title) },
        text = {
            Column {
                // Preview of picked color
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(48.dp)
                        .clip(RoundedCornerShape(12.dp))
                        .background(pickedColor)
                )

                Spacer(modifier = Modifier.height(16.dp))

                // Hue bar — tap to pick any hue
                Text("Pick a color", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))

                Canvas(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(40.dp)
                        .clip(RoundedCornerShape(8.dp))
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                val hue = (offset.x / size.width) * 360f
                                val color = Color.hsv(hue.coerceIn(0f, 360f), 0.75f, 0.8f)
                                pickedColor = color
                                hexInput = colorToHex(color)
                            }
                        }
                ) {
                    val step = size.width / 360f
                    for (h in 0..360) {
                        drawRect(
                            color = Color.hsv(h.toFloat(), 0.75f, 0.8f),
                            topLeft = Offset(h * step, 0f),
                            size = Size(step + 1f, size.height)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Suggested presets
                Text("Presets", style = MaterialTheme.typography.labelLarge,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
                Spacer(modifier = Modifier.height(8.dp))

                // 3 rows of 6 palette colors
                paletteColors.chunked(6).forEach { row ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 3.dp),
                        horizontalArrangement = Arrangement.SpaceEvenly
                    ) {
                        row.forEach { c ->
                            val hex = colorToHex(c)
                            val isSelected = hex == hexInput
                            Box(
                                contentAlignment = Alignment.Center,
                                modifier = Modifier
                                    .size(38.dp)
                                    .clip(CircleShape)
                                    .background(c)
                                    .then(
                                        if (isSelected) Modifier.border(3.dp, MaterialTheme.colorScheme.onBackground, CircleShape)
                                        else Modifier
                                    )
                                    .clickable {
                                        pickedColor = c
                                        hexInput = hex
                                    }
                            ) {
                                if (isSelected) {
                                    Icon(Icons.Default.Check, null, tint = Color.White, modifier = Modifier.size(16.dp))
                                }
                            }
                        }
                    }
                }

                Spacer(modifier = Modifier.height(16.dp))

                // Hex input
                OutlinedTextField(
                    value = hexInput,
                    onValueChange = {
                        val cleaned = it.replace("#", "").take(6)
                        hexInput = cleaned
                        hexToColor(cleaned)?.let { c -> pickedColor = c }
                    },
                    label = { Text("Hex color") },
                    prefix = { Text("#") },
                    singleLine = true,
                    keyboardOptions = KeyboardOptions(capitalization = KeyboardCapitalization.Characters),
                    modifier = Modifier.fillMaxWidth()
                )
            }
        },
        confirmButton = {
            TextButton(onClick = {
                onSelect(hexInput)
                onDismiss()
            }) {
                Text("Apply")
            }
        },
        dismissButton = {
            TextButton(onClick = onDismiss) {
                Text("Cancel")
            }
        }
    )
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
        Switch(checked = checked, onCheckedChange = onCheckedChange)
    }
}

private fun BreakReminderMode.displayName(): String = when (this) {
    BreakReminderMode.FULL_SCREEN -> "Full Screen"
    BreakReminderMode.OVERLAY_POPUP -> "Overlay Popup"
    BreakReminderMode.NOTIFICATION -> "Notification"
    BreakReminderMode.SILENT -> "Silent"
}
