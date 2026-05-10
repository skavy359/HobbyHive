package com.example.hobbyhive.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Fill
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.math.sqrt

// ═══════════════════════════════════════════════════
// BeeDrawables — Cute bee mascot & honeycomb shapes
// Canvas-drawn vector art for the handmade aesthetic
// ═══════════════════════════════════════════════════

/**
 * Draws a cute kawaii bee mascot.
 * The bee is drawn centered in the canvas.
 */
@Composable
fun BeeMascot(
    modifier: Modifier = Modifier,
    size: Dp = 80.dp
) {
    Canvas(modifier = modifier.size(size)) {
        val w = this.size.width
        val h = this.size.height
        val cx = w / 2f
        val cy = h / 2f
        val bodyW = w * 0.45f
        val bodyH = h * 0.35f

        // Wings (behind body)
        val wingColor = Color(0x40FFFFFF)
        val wingStroke = Color(0xFF1A1A1A)
        // Left wing
        drawOval(
            color = Color(0xFFE3F2FD),
            topLeft = Offset(cx - bodyW * 1.1f, cy - bodyH * 1.4f),
            size = Size(bodyW * 0.9f, bodyH * 1.2f)
        )
        drawOval(
            color = wingStroke,
            topLeft = Offset(cx - bodyW * 1.1f, cy - bodyH * 1.4f),
            size = Size(bodyW * 0.9f, bodyH * 1.2f),
            style = Stroke(width = w * 0.025f)
        )
        // Right wing
        drawOval(
            color = Color(0xFFE3F2FD),
            topLeft = Offset(cx + bodyW * 0.2f, cy - bodyH * 1.4f),
            size = Size(bodyW * 0.9f, bodyH * 1.2f)
        )
        drawOval(
            color = wingStroke,
            topLeft = Offset(cx + bodyW * 0.2f, cy - bodyH * 1.4f),
            size = Size(bodyW * 0.9f, bodyH * 1.2f),
            style = Stroke(width = w * 0.025f)
        )

        // Body (yellow oval)
        drawOval(
            color = HoneyYellow,
            topLeft = Offset(cx - bodyW, cy - bodyH),
            size = Size(bodyW * 2f, bodyH * 2f)
        )
        // Body outline
        drawOval(
            color = InkBlack,
            topLeft = Offset(cx - bodyW, cy - bodyH),
            size = Size(bodyW * 2f, bodyH * 2f),
            style = Stroke(width = w * 0.03f)
        )

        // Stripes
        val stripeW = bodyW * 2f * 0.18f
        for (i in 0..2) {
            val sx = cx - bodyW * 0.15f + i * stripeW * 1.5f
            drawRoundRect(
                color = InkBlack,
                topLeft = Offset(sx, cy - bodyH * 0.6f),
                size = Size(stripeW, bodyH * 1.2f),
                cornerRadius = androidx.compose.ui.geometry.CornerRadius(4.dp.toPx())
            )
        }

        // Head (circle, slightly above body)
        val headR = w * 0.2f
        val headCy = cy - bodyH * 0.7f
        drawCircle(
            color = HoneyYellow,
            radius = headR,
            center = Offset(cx, headCy)
        )
        drawCircle(
            color = InkBlack,
            radius = headR,
            center = Offset(cx, headCy),
            style = Stroke(width = w * 0.03f)
        )

        // Eyes (kawaii style — big dots)
        val eyeR = headR * 0.22f
        val eyeY = headCy - headR * 0.1f
        drawCircle(color = InkBlack, radius = eyeR, center = Offset(cx - headR * 0.35f, eyeY))
        drawCircle(color = InkBlack, radius = eyeR, center = Offset(cx + headR * 0.35f, eyeY))
        // Eye shine
        drawCircle(color = Color.White, radius = eyeR * 0.4f, center = Offset(cx - headR * 0.35f + eyeR * 0.2f, eyeY - eyeR * 0.2f))
        drawCircle(color = Color.White, radius = eyeR * 0.4f, center = Offset(cx + headR * 0.35f + eyeR * 0.2f, eyeY - eyeR * 0.2f))

        // Smile (cute arc)
        val smilePath = Path().apply {
            val smileY = headCy + headR * 0.15f
            moveTo(cx - headR * 0.25f, smileY)
            quadraticTo(cx, smileY + headR * 0.3f, cx + headR * 0.25f, smileY)
        }
        drawPath(smilePath, InkBlack, style = Stroke(width = w * 0.025f, cap = StrokeCap.Round))

        // Cheeks (kawaii blush)
        drawCircle(color = PastelPink.copy(alpha = 0.5f), radius = headR * 0.15f, center = Offset(cx - headR * 0.6f, headCy + headR * 0.15f))
        drawCircle(color = PastelPink.copy(alpha = 0.5f), radius = headR * 0.15f, center = Offset(cx + headR * 0.6f, headCy + headR * 0.15f))

        // Antennae
        val antennaStroke = Stroke(width = w * 0.02f, cap = StrokeCap.Round)
        // Left
        drawLine(InkBlack, Offset(cx - headR * 0.3f, headCy - headR * 0.8f), Offset(cx - headR * 0.6f, headCy - headR * 1.4f), strokeWidth = w * 0.02f)
        drawCircle(InkBlack, radius = w * 0.025f, center = Offset(cx - headR * 0.6f, headCy - headR * 1.4f))
        // Right
        drawLine(InkBlack, Offset(cx + headR * 0.3f, headCy - headR * 0.8f), Offset(cx + headR * 0.6f, headCy - headR * 1.4f), strokeWidth = w * 0.02f)
        drawCircle(InkBlack, radius = w * 0.025f, center = Offset(cx + headR * 0.6f, headCy - headR * 1.4f))

        // Stinger (tiny triangle at bottom)
        val stingerPath = Path().apply {
            moveTo(cx - bodyW * 0.1f, cy + bodyH)
            lineTo(cx, cy + bodyH + h * 0.06f)
            lineTo(cx + bodyW * 0.1f, cy + bodyH)
            close()
        }
        drawPath(stingerPath, InkBlack)
    }
}

/**
 * Draws a single hexagonal honeycomb cell.
 */
fun DrawScope.drawHexagonCell(
    center: Offset,
    radius: Float,
    fillColor: Color,
    strokeColor: Color = InkBlack,
    strokeWidth: Float = 2.5f
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
    drawPath(path, strokeColor, style = Stroke(width = strokeWidth, join = StrokeJoin.Round))
}

/**
 * Draws a hand-drawn squiggly line decoration.
 */
fun DrawScope.drawSquiggle(
    start: Offset,
    end: Offset,
    color: Color,
    strokeWidth: Float = 2f,
    waves: Int = 4,
    amplitude: Float = 8f
) {
    val path = Path()
    path.moveTo(start.x, start.y)
    val dx = (end.x - start.x) / (waves * 2)
    val dy = (end.y - start.y) / (waves * 2)
    for (i in 0 until waves) {
        val cpX1 = start.x + dx * (i * 2 + 1) + amplitude
        val cpY1 = start.y + dy * (i * 2 + 1) - amplitude
        val x1 = start.x + dx * (i * 2 + 1)
        val y1 = start.y + dy * (i * 2 + 1)
        path.quadraticTo(cpX1, cpY1, x1, y1)
        val cpX2 = start.x + dx * (i * 2 + 2) - amplitude
        val cpY2 = start.y + dy * (i * 2 + 2) + amplitude
        val x2 = start.x + dx * (i * 2 + 2)
        val y2 = start.y + dy * (i * 2 + 2)
        path.quadraticTo(cpX2, cpY2, x2, y2)
    }
    drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round))
}

/**
 * Draws decorative stars (hand-drawn feel).
 */
fun DrawScope.drawDoodleStar(
    center: Offset,
    outerRadius: Float,
    color: Color,
    strokeWidth: Float = 2.5f,
    points: Int = 4
) {
    val path = Path()
    val innerRadius = outerRadius * 0.4f
    for (i in 0 until points * 2) {
        val r = if (i % 2 == 0) outerRadius else innerRadius
        val angle = Math.toRadians((i * 360.0 / (points * 2)) - 90)
        val px = center.x + r * cos(angle).toFloat()
        val py = center.y + r * sin(angle).toFloat()
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(path, color, style = Stroke(width = strokeWidth, cap = StrokeCap.Round, join = StrokeJoin.Round))
}
