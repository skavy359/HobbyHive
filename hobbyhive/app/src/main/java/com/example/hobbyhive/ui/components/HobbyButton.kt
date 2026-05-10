package com.example.hobbyhive.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.ui.theme.*

// ═══════════════════════════════════════════════════
// HobbyButton — Chunky outlined primary button
// Thick border, flat bold color, playful
// ═══════════════════════════════════════════════════

@Composable
fun HobbyButton(
    text: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    isLoading: Boolean = false,
    gradientStart: Color = HoneyYellow,
    gradientEnd: Color = HoneyGold
) {
    Button(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp),
        enabled = enabled && !isLoading,
        shape = RoundedCornerShape(16.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = if (enabled && !isLoading) HoneyYellow else Color.Gray.copy(alpha = 0.3f),
            disabledContainerColor = Color.Gray.copy(alpha = 0.3f)
        ),
        border = BorderStroke(2.5.dp, if (enabled) InkBlack else Color.Gray),
        contentPadding = PaddingValues()
    ) {
        Box(
            modifier = Modifier.fillMaxSize(),
            contentAlignment = Alignment.Center
        ) {
            if (isLoading) {
                CircularProgressIndicator(
                    modifier = Modifier.size(24.dp),
                    color = InkBlack,
                    strokeWidth = 2.5.dp
                )
            } else {
                Text(
                    text = text,
                    color = InkBlack,
                    fontSize = 16.sp,
                    fontWeight = FontWeight.ExtraBold,
                    letterSpacing = 0.5.sp
                )
            }
        }
    }
}
