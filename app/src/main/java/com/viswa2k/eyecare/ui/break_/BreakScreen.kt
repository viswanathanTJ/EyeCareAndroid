package com.viswa2k.eyecare.ui.break_

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.viswa2k.eyecare.domain.BreakTipsProvider
import com.viswa2k.eyecare.ui.break_.components.CountdownCircle
import com.viswa2k.eyecare.ui.break_.components.EyeCareTip
import org.koin.androidx.compose.koinViewModel

private enum class ConfirmAction { SKIP, SNOOZE }

@Composable
fun BreakScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BreakViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()
    val breaksToday by viewModel.breaksToday.collectAsStateWithLifecycle()

    // Show celebration screen when break finishes
    if (uiState.isFinished) {
        BreakCompleteScreen(
            breaksToday = breaksToday,
            onDismiss = onDismiss
        )
        return
    }

    var confirmAction by remember { mutableStateOf<ConfirmAction?>(null) }
    val confirmQuote = remember(confirmAction) { BreakTipsProvider.getRandomTip() }

    // Confirmation dialog
    confirmAction?.let { action ->
        AlertDialog(
            onDismissRequest = { confirmAction = null },
            title = {
                Text(
                    text = if (action == ConfirmAction.SKIP) "Skip this break?"
                           else "Snooze for ${uiState.snoozeDurationMinutes} min?",
                    style = MaterialTheme.typography.titleLarge
                )
            },
            text = {
                Text(
                    text = "\u201C$confirmQuote\u201D",
                    style = MaterialTheme.typography.bodyLarge,
                    fontStyle = FontStyle.Italic,
                    textAlign = TextAlign.Center,
                    color = MaterialTheme.colorScheme.primary,
                    modifier = Modifier.fillMaxWidth()
                )
            },
            confirmButton = {
                TextButton(onClick = {
                    confirmAction = null
                    when (action) {
                        ConfirmAction.SKIP -> viewModel.skip()
                        ConfirmAction.SNOOZE -> viewModel.snooze()
                    }
                    onDismiss()
                }) {
                    Text(if (action == ConfirmAction.SKIP) "Yes, Skip" else "Yes, Snooze")
                }
            },
            dismissButton = {
                TextButton(onClick = { confirmAction = null }) {
                    Text("Take the Break")
                }
            }
        )
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(24.dp),
        verticalArrangement = Arrangement.Center,
        horizontalAlignment = Alignment.CenterHorizontally
    ) {
        Text(
            text = if (uiState.isFinished) "Great job!" else "Time for an Eye Break!",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.primary
        )

        Spacer(modifier = Modifier.height(8.dp))

        Text(
            text = "Look at something 20 feet away",
            style = MaterialTheme.typography.bodyLarge,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )

        Spacer(modifier = Modifier.height(48.dp))

        CountdownCircle(
            remainingSeconds = uiState.remainingSeconds,
            progress = uiState.progress
        )

        Spacer(modifier = Modifier.height(32.dp))

        EyeCareTip(
            tip = uiState.tip,
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 16.dp)
        )

        Spacer(modifier = Modifier.height(32.dp))

        Row {
                OutlinedButton(onClick = { confirmAction = ConfirmAction.SKIP }) {
                    Text("Skip")
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(onClick = { confirmAction = ConfirmAction.SNOOZE }) {
                    Text("Snooze (${uiState.snoozeDurationMinutes}m)")
                }
            }
    }
}
