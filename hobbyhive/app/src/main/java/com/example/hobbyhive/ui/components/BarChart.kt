package com.example.hobbyhive.ui.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Text
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.ui.theme.*

// ═══════════════════════════════════════════════════
// BarChart — Chunky outlined horizontal bar chart
// Thick borders, ink labels
// ═══════════════════════════════════════════════════

data class BarChartItem(
    val label: String,
    val value: Float,
    val color: Color
)

@Composable
fun BarChart(
    items: List<BarChartItem>,
    modifier: Modifier = Modifier,
    maxValue: Float = items.maxOfOrNull { it.value } ?: 1f
) {
    Column(
        modifier = modifier,
        verticalArrangement = Arrangement.spacedBy(12.dp)
    ) {
        items.forEach { item ->
            val animatedWidth by animateFloatAsState(
                targetValue = if (maxValue > 0) (item.value / maxValue).coerceIn(0f, 1f) else 0f,
                animationSpec = tween(800),
                label = "bar_${item.label}"
            )

            Column {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = item.label,
                        fontSize = 13.sp,
                        fontWeight = FontWeight.ExtraBold,
                        color = InkBlack
                    )
                    Text(
                        text = String.format("%.1f", item.value),
                        fontSize = 13.sp,
                        fontWeight = FontWeight.Black,
                        color = InkBlack
                    )
                }
                Spacer(Modifier.height(4.dp))
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(10.dp)
                        .clip(RoundedCornerShape(5.dp))
                        .background(PaperWarm)
                        .border(1.5.dp, InkBlack.copy(alpha = 0.2f), RoundedCornerShape(5.dp))
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxHeight()
                            .fillMaxWidth(animatedWidth)
                            .clip(RoundedCornerShape(5.dp))
                            .background(item.color)
                    )
                }
            }
        }
    }
}
