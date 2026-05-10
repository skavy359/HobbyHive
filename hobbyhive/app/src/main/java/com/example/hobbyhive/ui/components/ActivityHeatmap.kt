package com.example.hobbyhive.ui.components

import androidx.compose.foundation.BorderStroke
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
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.ui.theme.*
import java.util.*
import kotlin.math.cos
import kotlin.math.sin

// ═══════════════════════════════════════════════════
// ActivityHeatmap — Honeycomb hexagonal contribution grid
// Bee-themed hexagonal cells instead of boring squares
// ═══════════════════════════════════════════════════

@Composable
fun ActivityHeatmap(
    dailyMinutes: Map<Long, Int>,
    modifier: Modifier = Modifier,
    weeks: Int = 24,
    baseColor: Color = HoneyYellow
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

    Column(modifier = modifier) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            Text("Less", fontSize = 10.sp, color = Charcoal, fontWeight = FontWeight.Bold)
            Text("More", fontSize = 10.sp, color = Charcoal, fontWeight = FontWeight.Bold)
        }
        Spacer(Modifier.height(4.dp))

        // Hexagonal honeycomb grid
        Canvas(
            modifier = Modifier
                .fillMaxWidth()
                .height((7 * 14 + 6 * 4).dp)
        ) {
            val hexRadius = 6.dp.toPx()
            val hexWidth = hexRadius * 2f
            val hexHeight = hexRadius * kotlin.math.sqrt(3f)
            val gapX = 2.dp.toPx()
            val gapY = 2.dp.toPx()

            days.forEachIndexed { index, (_, minutes) ->
                val col = index / 7
                val row = index % 7
                val offsetX = if (row % 2 == 1) (hexRadius + gapX / 2) else 0f
                val cx = col * (hexWidth + gapX) + hexRadius + offsetX
                val cy = row * (hexHeight * 0.75f + gapY) + hexRadius

                val intensity = if (minutes > 0) (minutes.toFloat() / maxMinutes).coerceIn(0.15f, 1f) else 0f
                val cellColor = if (intensity > 0) baseColor.copy(alpha = intensity) else PaperWarm

                drawHexCell(Offset(cx, cy), hexRadius, cellColor)
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
                    fontWeight = FontWeight.Bold,
                    color = Charcoal,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}

private fun androidx.compose.ui.graphics.drawscope.DrawScope.drawHexCell(
    center: Offset,
    radius: Float,
    fillColor: Color
) {
    val path = Path()
    for (i in 0 until 6) {
        val angle = Math.toRadians((i * 60 - 30).toDouble())
        val px = center.x + radius * cos(angle).toFloat()
        val py = center.y + radius * sin(angle).toFloat()
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(path, fillColor, style = Fill)
    drawPath(path, InkBlack.copy(alpha = 0.25f), style = Stroke(width = 1.dp.toPx()))
}
