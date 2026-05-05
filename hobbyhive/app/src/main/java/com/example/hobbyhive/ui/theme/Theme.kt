package com.example.hobbyhive.ui.theme

import android.app.Activity
import android.os.Build
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
// HobbyHive Theme — Light + Dark Mode
// ═══════════════════════════════════════════════════

private val LightColorScheme = lightColorScheme(
    primary = AccentPurple,
    onPrimary = SurfaceLight,
    primaryContainer = AccentBg,
    onPrimaryContainer = Primary,

    secondary = AccentTeal,
    onSecondary = SurfaceLight,
    secondaryContainer = TealBg,
    onSecondaryContainer = Primary,

    tertiary = AccentAmber,
    onTertiary = SurfaceLight,
    tertiaryContainer = AmberBg,
    onTertiaryContainer = Primary,

    error = ErrorRed,
    onError = SurfaceLight,
    errorContainer = Color(0xFFFCE4EC),
    onErrorContainer = Color(0xFF93000A),

    background = BackgroundLight,
    onBackground = OnBackgroundLight,
    surface = SurfaceLight,
    onSurface = OnSurfaceLight,
    surfaceVariant = RowAltLight,
    onSurfaceVariant = SubtextLight,

    outline = BorderLight,
    outlineVariant = Color(0xFFE0E0F0),

    inverseSurface = Primary,
    inverseOnSurface = SurfaceLight,
    inversePrimary = AccentPurpleLight,
)

private val DarkColorScheme = darkColorScheme(
    primary = AccentPurpleLight,
    onPrimary = Primary,
    primaryContainer = MidNavy,
    onPrimaryContainer = AccentPurpleLight,

    secondary = AccentTealLight,
    onSecondary = Primary,
    secondaryContainer = Color(0xFF003D4D),
    onSecondaryContainer = AccentTealLight,

    tertiary = AccentAmberLight,
    onTertiary = Primary,
    tertiaryContainer = Color(0xFF4D3800),
    onTertiaryContainer = AccentAmberLight,

    error = ErrorRedLight,
    onError = Primary,
    errorContainer = Color(0xFF93000A),
    onErrorContainer = ErrorRedLight,

    background = BackgroundDark,
    onBackground = OnBackgroundDark,
    surface = SurfaceDark,
    onSurface = OnSurfaceDark,
    surfaceVariant = RowAltDark,
    onSurfaceVariant = SubtextDark,

    outline = BorderDark,
    outlineVariant = Color(0xFF3A3A50),

    inverseSurface = OnBackgroundDark,
    inverseOnSurface = BackgroundDark,
    inversePrimary = AccentPurple,
)

@Composable
fun HobbyhiveTheme(
    darkTheme: Boolean = isSystemInDarkTheme(),
    content: @Composable () -> Unit
) {
    val colorScheme = if (darkTheme) DarkColorScheme else LightColorScheme

    // Set status bar color
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