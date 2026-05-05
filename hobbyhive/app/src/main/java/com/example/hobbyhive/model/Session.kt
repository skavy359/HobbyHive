package com.example.hobbyhive.model

import androidx.room.Entity
import androidx.room.ForeignKey
import androidx.room.Index
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════
// Session Entity — Time tracking for hobbies
// ═══════════════════════════════════════════════════

@Entity(
    tableName = "sessions",
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
data class Session(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val hobbyId: Long,
    val durationMinutes: Int,
    val sessionDate: Long,              // Date of the session (millis)
    val notes: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
