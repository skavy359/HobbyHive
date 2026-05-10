package com.example.hobbyhive.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════
// HobbyHive Shape System — Chunky & Playful
// Rounded imperfect rectangles, thick corners
// ═══════════════════════════════════════════════════

val HobbyHiveShapes = Shapes(
    extraSmall = RoundedCornerShape(6.dp),
    small = RoundedCornerShape(12.dp),
    medium = RoundedCornerShape(20.dp),    // Cards — chunky corners
    large = RoundedCornerShape(28.dp),     // Elevated cards, dialogs
    extraLarge = RoundedCornerShape(32.dp) // Bottom sheets
)

// Named shape aliases for semantic usage
val ButtonShape = RoundedCornerShape(16.dp)
val CardShape = RoundedCornerShape(20.dp)
val ElevatedCardShape = RoundedCornerShape(24.dp)
val TextFieldShape = RoundedCornerShape(14.dp)
val ChipShape = RoundedCornerShape(50)       // Pill shape
val BottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
val HexagonalChip = RoundedCornerShape(12.dp)
val StickerShape = RoundedCornerShape(18.dp)
