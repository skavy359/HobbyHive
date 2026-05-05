package com.example.hobbyhive.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════
// Reminder Entity — Hobby practice reminders
// ═══════════════════════════════════════════════════

@Entity(
    tableName = "reminders",
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
data class Reminder(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hobbyId: Long,
    val timeInMillis: Long,         // Time of day for reminder (millis since midnight)
    val isEnabled: Boolean = true,
    val label: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
