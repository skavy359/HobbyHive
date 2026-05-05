package com.example.hobbyhive.model

// ═══════════════════════════════════════════════════
// Achievement — Computed badge/trophy (not stored in Room)
// ═══════════════════════════════════════════════════

data class Achievement(
    val id: String,
    val title: String,
    val description: String,
    val shortDesc: String,
    val icon: String,                    // Icon identifier
    val theme: String,                   // Color theme (e.g., "orange", "indigo")
    val isUnlocked: Boolean = false,
    val progress: String = "",           // Display string like "3/7d"
    val targetValue: Int = 0,
    val currentValue: Int = 0
) {
    val progressPercent: Float
        get() = if (targetValue > 0) (currentValue.toFloat() / targetValue * 100f).coerceIn(0f, 100f) else 0f
}
