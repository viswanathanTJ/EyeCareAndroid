package com.viswa2k.eyecare.ui.stats.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.unit.dp
import com.viswa2k.eyecare.data.db.entity.DailyStats
import java.time.LocalDate
import java.time.format.TextStyle
import java.util.Locale

@Composable
fun WeeklyChart(
    weeklyStats: List<DailyStats>,
    modifier: Modifier = Modifier
) {
    val maxBreaks = weeklyStats.maxOfOrNull { it.breaksTaken } ?: 1
    val today = LocalDate.now()
    val last7Days = (6 downTo 0).map { today.minusDays(it.toLong()) }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 8.dp),
        horizontalArrangement = Arrangement.SpaceEvenly,
        verticalAlignment = Alignment.Bottom
    ) {
        last7Days.forEach { date ->
            val dateStr = date.toString()
            val stats = weeklyStats.find { it.date == dateStr }
            val breaks = stats?.breaksTaken ?: 0
            val barHeight = if (maxBreaks > 0) (breaks.toFloat() / maxBreaks) else 0f

            DayBar(
                dayLabel = date.dayOfWeek.getDisplayName(TextStyle.SHORT, Locale.getDefault()),
                value = breaks,
                fillRatio = barHeight,
                isToday = date == today
            )
        }
    }
}

@Composable
private fun DayBar(
    dayLabel: String,
    value: Int,
    fillRatio: Float,
    isToday: Boolean
) {
    val primaryColor = MaterialTheme.colorScheme.primary
    val surfaceVariantColor = MaterialTheme.colorScheme.surfaceVariant

    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        modifier = Modifier.width(40.dp)
    ) {
        Text(
            text = "$value",
            style = MaterialTheme.typography.labelLarge,
            color = if (isToday) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )

        Canvas(
            modifier = Modifier
                .width(24.dp)
                .height(100.dp)
                .padding(vertical = 4.dp)
        ) {
            val barWidth = size.width
            val barHeight = size.height

            // Background
            drawRoundRect(
                color = surfaceVariantColor,
                topLeft = Offset.Zero,
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4.dp.toPx())
            )

            // Fill
            val fillHeight = barHeight * fillRatio
            if (fillHeight > 0) {
                drawRoundRect(
                    color = primaryColor,
                    topLeft = Offset(0f, barHeight - fillHeight),
                    size = Size(barWidth, fillHeight),
                    cornerRadius = CornerRadius(4.dp.toPx())
                )
            }
        }

        Text(
            text = dayLabel,
            style = MaterialTheme.typography.labelLarge,
            color = if (isToday) MaterialTheme.colorScheme.primary
                    else MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}
