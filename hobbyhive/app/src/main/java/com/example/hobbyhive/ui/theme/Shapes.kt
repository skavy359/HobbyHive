package com.example.hobbyhive.ui.theme

import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Shapes
import androidx.compose.ui.unit.dp

// ═══════════════════════════════════════════════════
// HobbyHive Shape System
// ═══════════════════════════════════════════════════

val HobbyHiveShapes = Shapes(
    extraSmall = RoundedCornerShape(4.dp),
    small = RoundedCornerShape(8.dp),
    medium = RoundedCornerShape(16.dp),    // Cards
    large = RoundedCornerShape(24.dp),     // Elevated cards, dialogs
    extraLarge = RoundedCornerShape(32.dp) // Bottom sheets
)

// Named shape aliases for semantic usage
val ButtonShape = RoundedCornerShape(16.dp)
val CardShape = RoundedCornerShape(16.dp)
val ElevatedCardShape = RoundedCornerShape(24.dp)
val TextFieldShape = RoundedCornerShape(12.dp)
val ChipShape = RoundedCornerShape(20.dp)
val BottomSheetShape = RoundedCornerShape(topStart = 28.dp, topEnd = 28.dp)
