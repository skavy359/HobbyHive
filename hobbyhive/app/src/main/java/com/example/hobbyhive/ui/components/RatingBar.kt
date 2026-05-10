package com.example.hobbyhive.ui.components

import androidx.compose.foundation.gestures.detectHorizontalDragGestures
import androidx.compose.foundation.gestures.detectTapGestures
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.filled.StarBorder
import androidx.compose.material.icons.filled.StarHalf
import androidx.compose.material3.Icon
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.layout.onSizeChanged
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.example.hobbyhive.ui.theme.HoneyYellow

// ═══════════════════════════════════════════════════
// RatingBar — Honey-colored star rating
// ═══════════════════════════════════════════════════

@Composable
fun RatingBar(
    rating: Float,
    onRatingChanged: ((Float) -> Unit)? = null,
    modifier: Modifier = Modifier,
    maxStars: Int = 5,
    starSize: Dp = 32.dp,
    activeColor: Color = HoneyYellow,
    inactiveColor: Color = Color.Gray.copy(alpha = 0.3f),
) {
    var rowWidth by remember { mutableIntStateOf(0) }
    val isInteractive = onRatingChanged != null

    Row(
        modifier = modifier
            .onSizeChanged { rowWidth = it.width }
            .then(
                if (isInteractive) {
                    Modifier
                        .pointerInput(Unit) {
                            detectTapGestures { offset ->
                                if (rowWidth > 0) {
                                    val starWidth = rowWidth.toFloat() / maxStars
                                    val tappedStar = (offset.x / starWidth).toInt() + 1
                                    onRatingChanged?.invoke(tappedStar.coerceIn(1, maxStars).toFloat())
                                }
                            }
                        }
                        .pointerInput(Unit) {
                            detectHorizontalDragGestures { change, _ ->
                                change.consume()
                                if (rowWidth > 0) {
                                    val starWidth = rowWidth.toFloat() / maxStars
                                    val draggedRating = (change.position.x / starWidth)
                                        .coerceIn(0f, maxStars.toFloat())
                                    val rounded = (draggedRating * 2).toInt() / 2f
                                    onRatingChanged?.invoke(rounded.coerceIn(0.5f, maxStars.toFloat()))
                                }
                            }
                        }
                } else Modifier
            )
    ) {
        for (i in 1..maxStars) {
            val icon = when {
                i <= rating.toInt() -> Icons.Filled.Star
                i - 0.5f <= rating -> Icons.Filled.StarHalf
                else -> Icons.Filled.StarBorder
            }
            val tint = when {
                i <= rating.toInt() -> activeColor
                i - 0.5f <= rating -> activeColor
                else -> inactiveColor
            }
            Icon(
                imageVector = icon,
                contentDescription = "Star $i",
                modifier = Modifier.size(starSize),
                tint = tint
            )
        }
    }
}
