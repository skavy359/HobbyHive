package com.example.hobbyhive.model

import androidx.room.Entity
import androidx.room.PrimaryKey

// ═══════════════════════════════════════════════════
// User Entity — Local user storage
// ═══════════════════════════════════════════════════

@Entity(tableName = "users")
data class User(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val fullName: String,
    val email: String,
    val passwordHash: String,
    val about: String = "",
    val createdAt: Long = System.currentTimeMillis()
)
