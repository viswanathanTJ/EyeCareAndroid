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
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import org.koin.androidx.compose.koinViewModel
import com.viswa2k.eyecare.ui.break_.components.CountdownCircle
import com.viswa2k.eyecare.ui.break_.components.EyeCareTip

@Composable
fun BreakScreen(
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BreakViewModel = koinViewModel()
) {
    val uiState by viewModel.uiState.collectAsStateWithLifecycle()

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

        if (uiState.isFinished) {
            FilledTonalButton(onClick = onDismiss) {
                Text("Done")
            }
        } else {
            Row {
                OutlinedButton(onClick = {
                    viewModel.skip()
                    onDismiss()
                }) {
                    Text("Skip")
                }

                Spacer(modifier = Modifier.width(16.dp))

                OutlinedButton(onClick = {
                    viewModel.snooze()
                    onDismiss()
                }) {
                    Text("Snooze")
                }
            }
        }
    }
}
