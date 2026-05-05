package com.example.hobbyhive.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════
// StudyEvent Entity — Calendar-based scheduling
// ═══════════════════════════════════════════════════

enum class EventStatus(val displayName: String) {
    PLANNED("Planned"),
    COMPLETED("Done"),
    MISSED("Missed"),
    CANCELLED("Cancelled");

    companion object {
        fun fromDisplayName(name: String): EventStatus {
            return entries.find { it.displayName == name } ?: PLANNED
        }
    }
}

@Entity(
    tableName = "study_events",
    foreignKeys = [
        ForeignKey(
            entity = Hobby::class,
            parentColumns = ["id"],
            childColumns = ["hobbyId"],
            onDelete = ForeignKey.SET_NULL
        )
    ],
    indices = [Index("hobbyId")]
)
data class StudyEvent(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hobbyId: Long? = null,          // Optional link to hobby
    val title: String,
    val description: String = "",
    val startTime: Long,                // Start time (millis)
    val endTime: Long,                  // End time (millis)
    val color: String = "#6366f1",      // Hex color for calendar display
    val status: EventStatus = EventStatus.PLANNED,
    val createdAt: Long = System.currentTimeMillis()
)
