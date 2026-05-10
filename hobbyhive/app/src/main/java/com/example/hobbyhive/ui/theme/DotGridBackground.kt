package com.example.hobbyhive.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import kotlin.math.cos
import kotlin.math.sin
import kotlin.random.Random

// ═══════════════════════════════════════════════════
// DotGridBackground — Paper Grain + Doodle Decor
// Subtle paper texture with hand-drawn decorations
// ═══════════════════════════════════════════════════

@Composable
fun DotGridBackground(
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val grainColor = if (isDark) PaperGrainDark else PaperGrainLight
    val doodleColor = if (isDark) Color(0x0DFFFFFF) else Color(0x08000000)

    val density = LocalDensity.current
    val stepPx = with(density) { 24.dp.toPx() }
    val grainRadius = with(density) { 1.dp.toPx() }

    // Pre-compute random seed for consistent doodle positions
    val seed = remember { Random.nextInt() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        // Paper grain dots — sparse and subtle
        var x = 0f
        while (x < width) {
            var y = 0f
            while (y < height) {
                drawCircle(
                    color = grainColor,
                    radius = grainRadius,
                    center = Offset(x, y)
                )
                y += stepPx
            }
            x += stepPx
        }

        // Scattered mini decorations (tiny stars, dots)
        val rng = Random(seed)
        val decoCount = ((width * height) / (200.dp.toPx() * 200.dp.toPx())).toInt().coerceIn(3, 12)
        repeat(decoCount) {
            val cx = rng.nextFloat() * width
            val cy = rng.nextFloat() * height
            val kind = rng.nextInt(3)
            when (kind) {
                0 -> drawTinyStar(cx, cy, 4.dp.toPx(), doodleColor)
                1 -> drawTinyHexagon(cx, cy, 5.dp.toPx(), doodleColor)
                2 -> drawCircle(
                    color = doodleColor,
                    radius = 2.dp.toPx(),
                    center = Offset(cx, cy)
                )
            }
        }
    }
}

private fun DrawScope.drawTinyStar(cx: Float, cy: Float, radius: Float, color: Color) {
    val path = Path()
    for (i in 0 until 5) {
        val outerAngle = Math.toRadians((i * 72 - 90).toDouble())
        val innerAngle = Math.toRadians((i * 72 + 36 - 90).toDouble())
        val ox = cx + radius * cos(outerAngle).toFloat()
        val oy = cy + radius * sin(outerAngle).toFloat()
        val ix = cx + radius * 0.4f * cos(innerAngle).toFloat()
        val iy = cy + radius * 0.4f * sin(innerAngle).toFloat()
        if (i == 0) path.moveTo(ox, oy) else path.lineTo(ox, oy)
        path.lineTo(ix, iy)
    }
    path.close()
    drawPath(path, color, style = Stroke(width = 1.dp.toPx()))
}

private fun DrawScope.drawTinyHexagon(cx: Float, cy: Float, radius: Float, color: Color) {
    val path = Path()
    for (i in 0 until 6) {
        val angle = Math.toRadians((i * 60 - 30).toDouble())
        val px = cx + radius * cos(angle).toFloat()
        val py = cy + radius * sin(angle).toFloat()
        if (i == 0) path.moveTo(px, py) else path.lineTo(px, py)
    }
    path.close()
    drawPath(path, color, style = Stroke(width = 1.dp.toPx()))
}
