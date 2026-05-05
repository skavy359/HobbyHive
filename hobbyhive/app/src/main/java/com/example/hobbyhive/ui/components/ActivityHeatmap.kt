package com.example.hobbyhive.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import java.util.*

// ═══════════════════════════════════════════════════
// ActivityHeatmap — GitHub-style contribution grid
// ═══════════════════════════════════════════════════

@Composable
fun ActivityHeatmap(
    dailyMinutes: Map<Long, Int>,  // dayStartMillis -> minutes
    modifier: Modifier = Modifier,
    weeks: Int = 24,
    baseColor: Color = MaterialTheme.colorScheme.primary
) {
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    val days = mutableListOf<Pair<Long, Int>>()
    val cal = (today.clone() as Calendar).apply {
        add(Calendar.DAY_OF_YEAR, -(weeks * 7 - 1))
    }

    repeat(weeks * 7) {
        val dayKey = cal.timeInMillis
        days.add(dayKey to (dailyMinutes[dayKey] ?: 0))
        cal.add(Calendar.DAY_OF_YEAR, 1)
    }

    val maxMinutes = days.maxOfOrNull { it.second }?.coerceAtLeast(1) ?: 1
    val trackColor = MaterialTheme.colorScheme.surfaceVariant

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Less", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
            Text("More", fontSize = 10.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        }
        Spacer(Modifier.height(4.dp))

        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height((7 * 14 + 6 * 3).dp)
        ) {
            val cellSize = 12.dp.toPx()
            val gap = 3.dp.toPx()
            val cornerR = 3.dp.toPx()

            days.forEachIndexed { index, (_, minutes) ->
                val col = index / 7
                val row = index % 7
                val x = col * (cellSize + gap)
                val y = row * (cellSize + gap)

                val intensity = if (minutes > 0) (minutes.toFloat() / maxMinutes).coerceIn(0.15f, 1f) else 0f
                val cellColor = if (intensity > 0) baseColor.copy(alpha = intensity) else trackColor

                drawRoundRect(
                    color = cellColor,
                    topLeft = Offset(x, y),
                    size = Size(cellSize, cellSize),
                    cornerRadius = CornerRadius(cornerR, cornerR)
                )
            }
        }

        Spacer(Modifier.height(8.dp))
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            val dayLabels = listOf("Sun", "Mon", "Tue", "Wed", "Thu", "Fri", "Sat")
            dayLabels.forEach { label ->
                Text(
                    text = label,
                    fontSize = 9.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
            }
        }
    }
}
