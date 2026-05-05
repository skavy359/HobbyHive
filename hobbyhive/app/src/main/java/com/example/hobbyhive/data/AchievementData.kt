package com.example.hobbyhive.data

import com.example.hobbyhive.model.Achievement

// ═══════════════════════════════════════════════════
// Achievement Seed Data — Delete this file later when
// switching to dynamic-only achievement computation
// ═══════════════════════════════════════════════════

object AchievementData {

    fun getAchievements(
        currentStreak: Int = 0,
        longestStreak: Int = 0,
        totalHobbies: Int = 0,
        totalHoursLogged: Int = 0,
        completedHobbies: Int = 0,
        totalGoals: Int = 0,
        totalSessions: Int = 0
    ): List<Achievement> = listOf(
        Achievement(
            id = "fire-starter",
            title = "Fire Starter",
            description = "Maintain a 7-day practice streak",
            shortDesc = "7 days",
            icon = "flame",
            theme = "orange",
            isUnlocked = currentStreak >= 7,
            progress = "${currentStreak.coerceAtMost(7)}/7d",
            targetValue = 7,
            currentValue = currentStreak.coerceAtMost(7)
        ),
        Achievement(
            id = "unstoppable",
            title = "Unstoppable",
            description = "Maintain a 30-day practice streak",
            shortDesc = "30 days",
            icon = "flame",
            theme = "rose",
            isUnlocked = currentStreak >= 30,
            progress = "${currentStreak.coerceAtMost(30)}/30d",
            targetValue = 30,
            currentValue = currentStreak.coerceAtMost(30)
        ),
        Achievement(
            id = "hobby-collector",
            title = "Hobby Collector",
            description = "Track at least 5 different hobbies",
            shortDesc = "5 hobbies",
            icon = "lightbulb",
            theme = "indigo",
            isUnlocked = totalHobbies >= 5,
            progress = "${totalHobbies.coerceAtMost(5)}/5",
            targetValue = 5,
            currentValue = totalHobbies.coerceAtMost(5)
        ),
        Achievement(
            id = "century-club",
            title = "Century Club",
            description = "Log 100 total hours of practice",
            shortDesc = "100 hrs",
            icon = "clock",
            theme = "emerald",
            isUnlocked = totalHoursLogged >= 100,
            progress = "${totalHoursLogged.coerceAtMost(100)}/100h",
            targetValue = 100,
            currentValue = totalHoursLogged.coerceAtMost(100)
        ),
        Achievement(
            id = "goal-getter",
            title = "Goal Getter",
            description = "Create your first goal",
            shortDesc = "1 goal",
            icon = "target",
            theme = "fuchsia",
            isUnlocked = totalGoals >= 1,
            progress = "${totalGoals.coerceAtMost(1)}/1",
            targetValue = 1,
            currentValue = totalGoals.coerceAtMost(1)
        ),
        Achievement(
            id = "completion-master",
            title = "Completion Master",
            description = "Complete a hobby to 100%",
            shortDesc = "1 hobby",
            icon = "award",
            theme = "blue",
            isUnlocked = completedHobbies >= 1,
            progress = "${completedHobbies.coerceAtMost(1)}/1",
            targetValue = 1,
            currentValue = completedHobbies.coerceAtMost(1)
        ),
        Achievement(
            id = "dedicated-learner",
            title = "Dedicated Learner",
            description = "Log 50 practice sessions",
            shortDesc = "50 sessions",
            icon = "activity",
            theme = "cyan",
            isUnlocked = totalSessions >= 50,
            progress = "${totalSessions.coerceAtMost(50)}/50",
            targetValue = 50,
            currentValue = totalSessions.coerceAtMost(50)
        ),
        Achievement(
            id = "hobby-master",
            title = "Hobby Master",
            description = "Track 10 different hobbies",
            shortDesc = "10 hobbies",
            icon = "crown",
            theme = "amber",
            isUnlocked = totalHobbies >= 10,
            progress = "${totalHobbies.coerceAtMost(10)}/10",
            targetValue = 10,
            currentValue = totalHobbies.coerceAtMost(10)
        )
    )
}
