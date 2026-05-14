package com.example.hobbyhive.data

import android.content.Context
import androidx.room.Database
import androidx.room.Room
import androidx.room.RoomDatabase
import androidx.room.TypeConverters
import com.example.hobbyhive.model.Goal
import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.model.Reminder
import com.example.hobbyhive.model.Session
import com.example.hobbyhive.model.StudyEvent
import com.example.hobbyhive.model.User

import com.example.hobbyhive.model.ForumPost
import com.example.hobbyhive.model.ForumComment
import com.example.hobbyhive.model.HobbyGroup

// ═══════════════════════════════════════════════════
// HobbyHive Room Database — Singleton
// ═══════════════════════════════════════════════════

@Database(
    entities = [
        Hobby::class, User::class, Reminder::class, 
        Session::class, Goal::class, StudyEvent::class,
        ForumPost::class, ForumComment::class, HobbyGroup::class
    ],
     version = 4,
    exportSchema = false
)
@TypeConverters(Converters::class)
abstract class HobbyHiveDatabase : RoomDatabase() {

    abstract fun hobbyDao(): HobbyDao
    abstract fun userDao(): UserDao
    abstract fun reminderDao(): ReminderDao
    abstract fun sessionDao(): SessionDao
    abstract fun goalDao(): GoalDao
    abstract fun studyEventDao(): StudyEventDao
    abstract fun communityDao(): CommunityDao

    companion object {
        @Volatile
        private var INSTANCE: HobbyHiveDatabase? = null

        fun getDatabase(context: Context): HobbyHiveDatabase {
            return INSTANCE ?: synchronized(this) {
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    HobbyHiveDatabase::class.java,
                    "hobbyhive_database"
                )
                    .fallbackToDestructiveMigration()
                    .build()
                INSTANCE = instance
                instance
            }
        }
    }
}
