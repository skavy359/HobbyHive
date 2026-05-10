package com.example.hobbyhive.ui.theme

import android.app.Activity
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.ui.graphics.Color
import androidx.compose.runtime.Composable
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.graphics.toArgb
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat

// ═══════════════════════════════════════════════════
// HobbyHive Theme — Playful Editorial Style
// Warm cream paper, honey yellow primary, chunky ink
// ═══════════════════════════════════════════════════

private val LightColorScheme = lightColorScheme(
    primary = HoneyYellow,
    onPrimary = InkBlack,
    primaryContainer = HoneyLight,
    onPrimaryContainer = InkBlack,

    secondary = LimeGreen,
    onSecondary = InkBlack,
    secondaryContainer = LimeCardBg,
    onSecondaryContainer = InkBlack,

    tertiary = CyanSky,
    onTertiary = InkBlack,
    tertiaryContainer = CyanCardBg,
    onTertiaryContainer = InkBlack,

    error = ErrorRed,
    onError = Color.White,
    errorContainer = Color(0xFFFFEBEE),
    onErrorContainer = Color(0xFFB71C1C),

    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = RowAltLight,
    onSurfaceVariant = SubtextLight,

    outline = BorderLight,
    outlineVariant = Color(0xFFE0D8C8),

    inverseSurface = InkBlack,
    inverseOnSurface = PaperCream,
    inversePrimary = HoneyLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = HoneyYellow,
    onPrimary = InkBlack,
    primaryContainer = Color(0xFF5C4700),
    onPrimaryContainer = HoneyLight,

    secondary = LimeGreen,
    onSecondary = InkBlack,
    secondaryContainer = Color(0xFF3D4D00),
    onSecondaryContainer = LimeChip,

    tertiary = CyanBright,
    onTertiary = InkBlack,
    tertiaryContainer = Color(0xFF004D4D),
    onTertiaryContainer = CyanPale,

    error = ErrorRedLight,
    onError = InkBlack,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = ErrorRedLight,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = RowAltDark,
    onSurfaceVariant = SubtextDark,

    outline = BorderDark,
    outlineVariant = Color(0xFF444444),

    inverseSurface = OnBackgroundDark,
    inverseOnSurface = BackgroundDark,
    inversePrimary = HoneyGold,
)

@Composable
fun HobbyhiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Set status bar color to match warm paper
    val view = LocalView.current
    if (!view.isInEditMode) {
        SideEffect {
            val window = (view.context as Activity).window
            window.statusBarColor = colorScheme.background.toArgb()
            WindowCompat.getInsetsController(window, view).isAppearanceLightStatusBars = !darkTheme
        }
    }

    MaterialTheme(
        colorScheme = colorScheme,
        typography = Typography,
        shapes = HobbyHiveShapes,
        content = content
    )
}