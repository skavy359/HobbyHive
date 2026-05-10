package com.example.hobbyhive.ui.theme

import androidx.compose.ui.graphics.Color

// ═══════════════════════════════════════════════════
// HobbyHive — Playful Handmade Editorial Palette
// Bee-themed, bold, chunky, warm & expressive
// ═══════════════════════════════════════════════════

// ── Ink & Paper ──────────────────────────────────
val InkBlack = Color(0xFF1A1A1A)          // Thick outlines, bold text
val Charcoal = Color(0xFF333333)          // Body text
val PaperCream = Color(0xFFFFF8E7)        // Warm paper background
val PaperWhite = Color(0xFFFFFDF5)        // Card surfaces
val PaperWarm = Color(0xFFFFF3D6)         // Warmer variant

// ── Primary Honey Palette ────────────────────────
val HoneyYellow = Color(0xFFFFD93D)       // Primary CTA, bee energy
val HoneyGold = Color(0xFFFFC107)         // Deeper gold accent
val HoneyAmber = Color(0xFFFF9800)        // Warm amber for streaks
val HoneyLight = Color(0xFFFFECB3)        // Soft yellow bg

// ── Lime Green Accents ───────────────────────────
val LimeGreen = Color(0xFFC6F135)         // Vibrant lime (from reference)
val LimeMint = Color(0xFFB8E986)          // Softer mint lime
val LimeChip = Color(0xFFDDFF6B)          // Chip/badge lime

// ── Cyan / Sky ───────────────────────────────────
val CyanSky = Color(0xFF7FDBDA)           // Cards, backgrounds
val CyanBright = Color(0xFF40E0D0)        // Brighter cyan accent
val CyanPale = Color(0xFFE0F7FA)          // Pale cyan bg

// ── Pastel Accents ───────────────────────────────
val PastelPink = Color(0xFFFFB5C2)        // Fun card accent
val PastelPeach = Color(0xFFFFE5B4)       // Warm sections
val PastelLavender = Color(0xFFD4BBFF)    // Purple-ish accent
val PastelMint = Color(0xFFB2F5EA)        // Mint section

// ── Feedback Colors ──────────────────────────────
val SuccessGreen = Color(0xFF4CAF50)
val ErrorRed = Color(0xFFEF5350)
val ErrorRedLight = Color(0xFFFF8A80)
val WarningOrange = Color(0xFFFF9800)

// ── Surface & Background — Light Mode ────────────
val SurfaceLight = PaperWhite
val BackgroundLight = PaperCream
val OnBackgroundLight = InkBlack
val OnSurfaceLight = InkBlack
val SubtextLight = Color(0xFF666666)
val BorderLight = InkBlack                 // Thick black borders!
val CardLight = PaperWhite
val RowAltLight = Color(0xFFFFF5E6)

// ── Surface & Background — Dark Mode ─────────────
val SurfaceDark = Color(0xFF2A2A2A)
val BackgroundDark = Color(0xFF1E1E1E)
val OnBackgroundDark = Color(0xFFF5F0E3)
val OnSurfaceDark = Color(0xFFF5F0E3)
val SubtextDark = Color(0xFFAAAAAA)
val BorderDark = Color(0xFFF5F0E3)         // Light borders on dark
val CardDark = Color(0xFF333333)
val RowAltDark = Color(0xFF252525)

// ── Tinted Card Backgrounds ──────────────────────
val YellowCardBg = Color(0xFFFFF9E3)
val PinkCardBg = Color(0xFFFFECF0)
val CyanCardBg = Color(0xFFE6FAFA)
val GreenCardBg = Color(0xFFF0FFF0)
val PeachCardBg = Color(0xFFFFF0E0)
val LimeCardBg = Color(0xFFF5FFD6)

// ── Paper Texture Colors ─────────────────────────
val PaperGrainLight = Color(0x0A000000)   // Subtle grain dots
val PaperGrainDark = Color(0x08FFFFFF)

// ── Gradient helpers ─────────────────────────────
val GradientStart = HoneyYellow
val GradientEnd = HoneyGold

// ── Feature Page Accent Colors ───────────────────
// Sessions
val SessionGreen = Color(0xFF4CAF50)
val SessionTeal = CyanBright
val SessionCyan = CyanSky

// Goals
val GoalIndigo = PastelLavender
val GoalViolet = Color(0xFFB388FF)

// Analytics
val AnalyticsPurple = Color(0xFFCE93D8)
val AnalyticsFuchsia = PastelPink

// Planner
val PlannerIndigo = PastelLavender
val PlannerPurple = Color(0xFFE1BEE7)
val PlannerFuchsia = PastelPink

// Leaderboard
val LeaderAmber = HoneyYellow
val LeaderOrange = HoneyAmber
val LeaderRose = PastelPink

// Dashboard
val DashboardIndigo = HoneyYellow
val DashboardPurple = HoneyGold

// Streak
val StreakOrange = HoneyAmber
val StreakRose = PastelPink

// Status colors
val StatusPlanned = PastelLavender
val StatusCompleted = SuccessGreen
val StatusMissed = ErrorRed
val StatusCancelled = Color(0xFF9E9E9E)

// Achievement themes
val ThemeOrange = HoneyAmber
val ThemeIndigo = PastelLavender
val ThemeEmerald = Color(0xFF66BB6A)
val ThemeFuchsia = PastelPink
val ThemeBlue = CyanSky
val ThemeRose = PastelPink
val ThemeCyan = CyanBright
val ThemeAmberAch = HoneyYellow

// ── Playful accent — Outlined card shadow ────────
val PlayfulShadow = Color(0xFF1A1A1A)

// ── Accent Colors (Material3 compatibility) ──────
val AccentPurple = PastelLavender
val AccentPurpleLight = Color(0xFFE1BEE7)
val AccentTeal = CyanSky
val AccentTealLight = CyanBright
val AccentAmber = HoneyYellow
val AccentAmberLight = HoneyLight
val AccentBg = YellowCardBg
val AmberBg = Color(0xFFFFF8EC)
val TealBg = CyanCardBg
val GreenBg = GreenCardBg

// Dot grid (kept for compat but replaced by paper grain)
val DotGridLight = PaperGrainLight
val DotGridDark = PaperGrainDark