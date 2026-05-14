package com.example.hobbyhive.ui.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navArgument
import com.example.hobbyhive.appwrite.repository.AppwriteAuthRepository
import com.example.hobbyhive.appwrite.repository.AppwriteHobbyRepository
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.data.UserRepository
import com.example.hobbyhive.ui.screens.*

// ═══════════════════════════════════════════════════
// NavGraph — Central navigation wiring
// ═══════════════════════════════════════════════════

@Composable
fun NavGraph(
    navController: NavHostController,
    userPreferencesRepository: UserPreferencesRepository,
    userRepository: UserRepository, // Keep for legacy UI components if needed
    appwriteAuthRepository: AppwriteAuthRepository,
    hobbyRepository: AppwriteHobbyRepository,
    startDestination: String = Routes.Splash.route
) {
    NavHost(
        navController = navController,
        startDestination = startDestination
    ) {
        // Splash
        composable(Routes.Splash.route) {
            SplashScreen(
                userPreferencesRepository = userPreferencesRepository,
                onNavigateToHome = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                },
                onNavigateToOnboarding = {
                    navController.navigate(Routes.Onboarding.route) {
                        popUpTo(Routes.Splash.route) { inclusive = true }
                    }
                }
            )
        }

        // Onboarding
        composable(Routes.Onboarding.route) {
            OnboardingScreen(
                userPreferencesRepository = userPreferencesRepository,
                onFinish = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Onboarding.route) { inclusive = true }
                    }
                }
            )
        }

        // Login
        composable(Routes.Login.route) {
            LoginScreen(
                userRepository = userRepository,
                userPreferencesRepository = userPreferencesRepository,
                appwriteAuthRepository = appwriteAuthRepository,
                onLoginSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Login.route) { inclusive = true }
                    }
                },
                onNavigateToRegister = {
                    navController.navigate(Routes.Register.route)
                }
            )
        }

        // Register
        composable(Routes.Register.route) {
            RegisterScreen(
                userPreferencesRepository = userPreferencesRepository,
                appwriteAuthRepository = appwriteAuthRepository,
                onRegisterSuccess = {
                    navController.navigate(Routes.Home.route) {
                        popUpTo(Routes.Register.route) { inclusive = true }
                    }
                },
                onNavigateToLogin = {
                    navController.popBackStack()
                }
            )
        }

        // Main App Screen (with Bottom Nav)
        composable(Routes.Home.route) {
            MainScreen(
                parentNavController = navController,
                hobbyRepository = hobbyRepository,
                userPreferencesRepository = userPreferencesRepository,
                userRepository = userRepository
            )
        }

        // Leaderboard
        composable(Routes.Leaderboard.route) {
            LeaderboardScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Achievements
        composable(Routes.Achievements.route) {
            AchievementsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Category Browse
        composable(Routes.CategoryBrowse.route) {
            CategoryBrowseScreen(
                onCategorySelected = { category ->
                    // For now, pop back stack
                    navController.popBackStack()
                }
            )
        }

        // Hobby Detail
        composable(
            route = Routes.HobbyDetail.route,
            arguments = listOf(navArgument("hobbyId") { type = NavType.LongType })
        ) { backStackEntry ->
            val hobbyId = backStackEntry.arguments?.getLong("hobbyId") ?: 0L
            HobbyDetailScreen(
                hobbyId = hobbyId,
                hobbyRepository = hobbyRepository,
                onNavigateBack = { navController.popBackStack() },
                onEditHobby = { id ->
                    navController.navigate(Routes.EditHobby.createRoute(id))
                }
            )
        }

        // Add Hobby
        composable(Routes.AddHobby.route) {
            AddEditHobbyScreen(
                hobbyId = null,
                hobbyRepository = hobbyRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Edit Hobby
        composable(
            route = Routes.EditHobby.route,
            arguments = listOf(navArgument("hobbyId") { type = NavType.LongType })
        ) { backStackEntry ->
            val hobbyId = backStackEntry.arguments?.getLong("hobbyId") ?: 0L
            AddEditHobbyScreen(
                hobbyId = hobbyId,
                hobbyRepository = hobbyRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }

        // Settings
        composable(Routes.Settings.route) {
            SettingsScreen(
                userPreferencesRepository = userPreferencesRepository,
                onNavigateBack = { navController.popBackStack() },
                onLogout = {
                    navController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }

        // Community
        composable(Routes.Community.route) {
            CommunityScreen(
                onNavigateBack = { navController.popBackStack() },
                onNavigateToPost = { id -> navController.navigate(Routes.PostDetail.createRoute(id)) }
            )
        }

        // Post Detail
        composable(
            route = Routes.PostDetail.route,
            arguments = listOf(androidx.navigation.navArgument("postId") { type = androidx.navigation.NavType.LongType })
        ) { backStackEntry ->
            val postId = backStackEntry.arguments?.getLong("postId") ?: 0L
            PostDetailScreen(
                postId = postId,
                onNavigateBack = { navController.popBackStack() }
            )
        }



        // Notifications
        composable(Routes.Notifications.route) {
            NotificationsScreen(
                hobbyRepository = hobbyRepository,
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}
