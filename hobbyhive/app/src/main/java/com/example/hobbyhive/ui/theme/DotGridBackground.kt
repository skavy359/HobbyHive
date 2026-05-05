package com.example.hobbyhive.ui.theme

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════
// Dot Grid Background — Figma/FigJam inspired canvas
// Provides a subtle, premium textured background
// ═══════════════════════════════════════════════════

@Composable
fun DotGridBackground(
    modifier: Modifier = Modifier
) {
    val isDark = isSystemInDarkTheme()
    val dotColor = if (isDark) DotGridDark else DotGridLight

    val density = LocalDensity.current
    val stepPx = with(density) { 28.dp.toPx() }
    val radiusPx = with(density) { 1.5.dp.toPx() }

    Canvas(modifier = modifier.fillMaxSize()) {
        val width = size.width
        val height = size.height

        var x = 0f
        while (x < width) {
            var y = 0f
            while (y < height) {
                drawCircle(
                    color = dotColor,
                    radius = radiusPx,
                    center = Offset(x, y)
                )
                y += stepPx
            }
            x += stepPx
        }
    }
}
