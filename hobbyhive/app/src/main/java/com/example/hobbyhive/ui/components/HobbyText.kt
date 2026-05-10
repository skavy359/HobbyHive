package com.example.hobbyhive.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.TextUnit
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.ui.theme.HoneyGold
import com.example.hobbyhive.ui.theme.HoneyYellow
import com.example.hobbyhive.ui.theme.InkBlack

// ═══════════════════════════════════════════════════
// HobbyText — Custom text with optional honey gradient
// ═══════════════════════════════════════════════════

@Composable
fun HobbyText(
    text: String,
    modifier: Modifier = Modifier,
    style: TextStyle = MaterialTheme.typography.bodyLarge,
    color: Color = InkBlack,
    fontWeight: FontWeight? = null,
    fontSize: TextUnit = TextUnit.Unspecified,
    letterSpacing: TextUnit = TextUnit.Unspecified,
    useGradient: Boolean = false,
    maxLines: Int = Int.MAX_VALUE,
) {
    if (useGradient) {
        Text(
            text = text,
            modifier = modifier,
            style = style.copy(
                brush = Brush.linearGradient(
                    colors = listOf(HoneyYellow, HoneyGold),
                    start = Offset.Zero,
                    end = Offset(200f, 0f)
                ),
                fontWeight = fontWeight ?: style.fontWeight,
                fontSize = if (fontSize != TextUnit.Unspecified) fontSize else style.fontSize,
                letterSpacing = if (letterSpacing != TextUnit.Unspecified) letterSpacing else style.letterSpacing
            ),
            maxLines = maxLines
        )
    } else {
        Text(
            text = text,
            modifier = modifier,
            style = style,
            color = color,
            fontWeight = fontWeight,
            fontSize = if (fontSize != TextUnit.Unspecified) fontSize else TextUnit.Unspecified,
            letterSpacing = if (letterSpacing != TextUnit.Unspecified) letterSpacing else TextUnit.Unspecified,
            maxLines = maxLines
        )
    }
}
