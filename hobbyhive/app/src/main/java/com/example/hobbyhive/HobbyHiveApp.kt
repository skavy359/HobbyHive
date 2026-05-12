package com.example.hobbyhive

import android.app.Application
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import com.example.hobbyhive.model.AppwriteClient

class HobbyHiveApp : Application() {

    companion object {
        const val CHANNEL_REMINDERS = "hobby_reminders"
        const val CHANNEL_MILESTONES = "milestones"
        const val CHANNEL_GENERAL = "general"
    }

    override fun onCreate() {
        super.onCreate()
        AppwriteClient.init(this)
        createNotificationChannels()
    }

    private fun createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val manager = getSystemService(NotificationManager::class.java)

            val reminderChannel = NotificationChannel(
                CHANNEL_REMINDERS, "Hobby Reminders", NotificationManager.IMPORTANCE_HIGH
            ).apply { description = "Daily practice reminders for your hobbies" }

            val milestoneChannel = NotificationChannel(
                CHANNEL_MILESTONES, "Achievements", NotificationManager.IMPORTANCE_DEFAULT
            ).apply { description = "Milestone reached notifications" }

            val generalChannel = NotificationChannel(
                CHANNEL_GENERAL, "General", NotificationManager.IMPORTANCE_LOW
            ).apply { description = "App updates and tips" }

            manager.createNotificationChannels(listOf(reminderChannel, milestoneChannel, generalChannel))
        }
    }
}
