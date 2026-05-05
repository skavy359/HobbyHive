package com.example.hobbyhive.data

import androidx.room.TypeConverter
import com.example.hobbyhive.model.EventStatus
import com.example.hobbyhive.model.GoalStatus
import com.example.hobbyhive.model.HobbyCategory
import com.example.hobbyhive.model.HobbyStatus

// ═══════════════════════════════════════════════════
// Room Type Converters — Enum ↔ String
// ═══════════════════════════════════════════════════

class Converters {
    @TypeConverter
    fun fromHobbyCategory(category: HobbyCategory): String = category.name

    @TypeConverter
    fun toHobbyCategory(name: String): HobbyCategory = HobbyCategory.valueOf(name)

    @TypeConverter
    fun fromHobbyStatus(status: HobbyStatus): String = status.name

    @TypeConverter
    fun toHobbyStatus(name: String): HobbyStatus = HobbyStatus.valueOf(name)

    @TypeConverter
    fun fromGoalStatus(status: GoalStatus): String = status.name

    @TypeConverter
    fun toGoalStatus(name: String): GoalStatus = GoalStatus.valueOf(name)

    @TypeConverter
    fun fromEventStatus(status: EventStatus): String = status.name

    @TypeConverter
    fun toEventStatus(name: String): EventStatus = EventStatus.valueOf(name)
}
