package com.example.hobbyhive.model

// ═══════════════════════════════════════════════════
// Hobby Categories — Domain enum for categorization
// ═══════════════════════════════════════════════════

enum class HobbyCategory(val displayName: String, val emoji: String) {
    ART("Art & Drawing", "🎨"),
    MUSIC("Music", "🎵"),
    SPORTS("Sports", "⚽"),
    TECH("Technology", "💻"),
    COOKING("Cooking", "🍳"),
    READING("Reading", "📚"),
    GAMING("Gaming", "🎮"),
    FITNESS("Fitness", "💪"),
    PHOTOGRAPHY("Photography", "📷"),
    OTHER("Other", "🌟");

    companion object {
        fun fromDisplayName(name: String): HobbyCategory {
            return entries.find { it.displayName == name } ?: OTHER
        }
    }
}

// Hobby status for filtering
enum class HobbyStatus(val displayName: String) {
    ACTIVE("Active"),
    COMPLETED("Completed"),
    ARCHIVED("Archived");

    companion object {
        fun fromDisplayName(name: String): HobbyStatus {
            return entries.find { it.displayName == name } ?: ACTIVE
        }
    }
}
