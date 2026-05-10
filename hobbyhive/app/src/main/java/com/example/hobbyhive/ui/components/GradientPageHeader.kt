package com.example.hobbyhive.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.ui.theme.*

// ═══════════════════════════════════════════════════
// GradientPageHeader — Sticker-style hero banner
// Flat color, thick border, doodle decorations
// ═══════════════════════════════════════════════════

@Composable
fun GradientPageHeader(
    title: String,
    subtitle: String,
    badgeIcon: ImageVector,
    badgeText: String,
    gradientColors: List<Color>,
    modifier: Modifier = Modifier,
    actionButton: (@Composable () -> Unit)? = null
) {
    val bgColor = gradientColors.firstOrNull() ?: HoneyYellow

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(24.dp))
            .background(bgColor.copy(alpha = 0.3f))
    ) {
        // Thick outline
        Canvas(modifier = Modifier.matchParentSize()) {
            drawRoundRect(
                color = InkBlack,
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(24.dp.toPx()),
                style = Stroke(width = 3.dp.toPx())
            )
            // Decorative doodle star
            drawDoodleStar(
                center = Offset(size.width - 40.dp.toPx(), 30.dp.toPx()),
                outerRadius = 12.dp.toPx(),
                color = InkBlack.copy(alpha = 0.15f),
                strokeWidth = 2.dp.toPx()
            )
        }

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(horizontal = 22.dp, vertical = 28.dp)
        ) {
            // Badge pill
            Surface(
                shape = RoundedCornerShape(50),
                color = Color.White.copy(alpha = 0.5f),
                border = BorderStroke(2.dp, InkBlack)
            ) {
                Row(
                    modifier = Modifier.padding(horizontal = 12.dp, vertical = 6.dp),
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(6.dp)
                ) {
                    Icon(
                        imageVector = badgeIcon,
                        contentDescription = null,
                        tint = InkBlack,
                        modifier = Modifier.size(14.dp)
                    )
                    Text(
                        text = badgeText,
                        color = InkBlack,
                        fontSize = 11.sp,
                        fontWeight = FontWeight.ExtraBold,
                        letterSpacing = 0.5.sp
                    )
                }
            }

            Spacer(Modifier.height(14.dp))

            Text(
                text = title,
                color = InkBlack,
                fontSize = 30.sp,
                fontWeight = FontWeight.Black,
                lineHeight = 34.sp,
                letterSpacing = (-0.5).sp
            )

            Spacer(Modifier.height(6.dp))

            Text(
                text = subtitle,
                color = Charcoal,
                fontSize = 14.sp,
                fontWeight = FontWeight.Medium,
                lineHeight = 20.sp
            )

            if (actionButton != null) {
                Spacer(Modifier.height(16.dp))
                actionButton()
            }
        }
    }
}
