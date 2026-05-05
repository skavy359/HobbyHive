package com.example.hobbyhive.ui.navigation

// ═══════════════════════════════════════════════════
// Routes — Sealed class for type-safe navigation
// ═══════════════════════════════════════════════════

sealed class Routes(val route: String) {
    data object Splash : Routes("splash")
    data object Login : Routes("login")
    data object Register : Routes("register")
    data object Onboarding : Routes("onboarding")
    data object Home : Routes("home")
    data object HobbyDetail : Routes("hobby_detail/{hobbyId}") {
        fun createRoute(hobbyId: Long) = "hobby_detail/$hobbyId"
    }
    data object AddHobby : Routes("add_hobby")
    data object EditHobby : Routes("edit_hobby/{hobbyId}") {
        fun createRoute(hobbyId: Long) = "edit_hobby/$hobbyId"
    }
    data object Settings : Routes("settings")
    data object Profile : Routes("profile")
    data object Notifications : Routes("notifications")
    data object Sessions : Routes("sessions")
    data object Goals : Routes("goals")
    data object Analytics : Routes("analytics")
    data object Planner : Routes("planner")
    data object Leaderboard : Routes("leaderboard")
    data object Achievements : Routes("achievements")
    data object CategoryBrowse : Routes("category_browse")
    data object Community : Routes("community")
    data object PostDetail : Routes("post_detail/{postId}") {
        fun createRoute(postId: Long) = "post_detail/$postId"
    }
}
