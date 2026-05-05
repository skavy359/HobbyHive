package com.example.hobbyhive.model

import androidx.room.Entity
import androidx.room.PrimaryKey
import androidx.room.TypeConverters
import com.example.hobbyhive.data.Converters

// ═══════════════════════════════════════════════════
// Hobby Entity — Core data model
// ═══════════════════════════════════════════════════

@Entity(tableName = "hobbies")
@TypeConverters(Converters::class)
data class Hobby(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val description: String = "",
    val category: HobbyCategory = HobbyCategory.OTHER,
    val rating: Float = 0f,
    val progress: Int = 0,               // 0–100 percent
    val status: HobbyStatus = HobbyStatus.ACTIVE,
    val notes: String = "",
    val imageUri: String? = null,
    val reminderEnabled: Boolean = false,
    val reminderTime: Long? = null,       // Millis since epoch
    val targetDate: Long? = null,         // Optional goal target date
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)
