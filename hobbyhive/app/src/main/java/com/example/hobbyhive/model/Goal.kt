package com.example.hobbyhive.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════
// Goal Entity — Target tracking for hobbies
// ═══════════════════════════════════════════════════

enum class GoalStatus(val displayName: String) {
    IN_PROGRESS("In Progress"),
    COMPLETED("Completed"),
    AT_RISK("At Risk"),
    FAILED("Failed");

    companion object {
        fun fromDisplayName(name: String): GoalStatus {
            return entries.find { it.displayName == name } ?: IN_PROGRESS
        }
    }
}

@Entity(
    tableName = "goals",
    foreignKeys = [
        ForeignKey(
            entity = Hobby::class,
            parentColumns = ["id"],
            childColumns = ["hobbyId"],
            onDelete = ForeignKey.CASCADE
        )
    ],
    indices = [Index("hobbyId")]
)
data class Goal(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val appwriteId: String? = null,
    val hobbyId: Long,
    val title: String,
    val description: String = "",
    val targetValue: Int = 100,         // Target progress (e.g., 100 hours)
    val currentValue: Int = 0,          // Current progress
    val unit: String = "hours",         // "hours", "sessions", "percent"
    val deadline: Long? = null,         // Optional deadline (millis)
    val status: GoalStatus = GoalStatus.IN_PROGRESS,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
) {
    val progressPercent: Float
        get() = if (targetValue > 0) (currentValue.toFloat() / targetValue * 100f).coerceIn(0f, 100f) else 0f
}
