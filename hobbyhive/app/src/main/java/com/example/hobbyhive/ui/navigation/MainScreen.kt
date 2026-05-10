package com.example.hobbyhive.ui.navigation

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.material3.DrawerValue
import androidx.compose.material3.rememberDrawerState
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.ui.screens.*
import com.example.hobbyhive.ui.theme.*
import kotlinx.coroutines.launch

@Composable
fun MainScreen(
    parentNavController: NavHostController,
    hobbyRepository: HobbyRepository,
    userPreferencesRepository: com.example.hobbyhive.data.UserPreferencesRepository,
    userRepository: com.example.hobbyhive.data.UserRepository
) {
    val bottomNavController = rememberNavController()
    val navBackStackEntry by bottomNavController.currentBackStackEntryAsState()
    val currentRoute = navBackStackEntry?.destination?.route
    val drawerState = rememberDrawerState(initialValue = DrawerValue.Closed)
    val scope = rememberCoroutineScope()

    val bottomNavItems = listOf(
        BottomNavItem(Routes.Home.route, "Home", Icons.Default.Home),
        BottomNavItem(Routes.Sessions.route, "Sessions", Icons.Default.Timer),
        BottomNavItem(Routes.Goals.route, "Goals", Icons.Default.Flag),
        BottomNavItem(Routes.Planner.route, "Planner", Icons.Default.CalendarMonth),
        BottomNavItem(Routes.Profile.route, "Profile", Icons.Default.Person)
    )

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            com.example.hobbyhive.ui.components.DrawerContent(
                onItemClick = { route ->
                    scope.launch { drawerState.close() }
                    parentNavController.navigate(route)
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    parentNavController.navigate(Routes.Login.route) {
                        popUpTo(Routes.Home.route) { inclusive = true }
                    }
                }
            )
        }
    ) {
        Scaffold(
            containerColor = PaperCream,
            bottomBar = {
                // Playful editorial bottom bar with thick border
                NavigationBar(
                    containerColor = PaperWhite,
                    tonalElevation = 0.dp,
                    modifier = Modifier
                        .border(
                            width = 2.5.dp,
                            color = InkBlack,
                            shape = RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp)
                        )
                        .clip(RoundedCornerShape(topStart = 20.dp, topEnd = 20.dp))
                ) {
                    bottomNavItems.forEach { item ->
                        NavigationBarItem(
                            icon = { Icon(item.icon, contentDescription = item.title) },
                            label = {
                                Text(
                                    item.title,
                                    fontWeight = FontWeight.ExtraBold,
                                    fontSize = 10.sp
                                )
                            },
                            selected = currentRoute == item.route,
                            onClick = {
                                bottomNavController.navigate(item.route) {
                                    popUpTo(bottomNavController.graph.findStartDestination().id) { saveState = true }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            },
                            colors = NavigationBarItemDefaults.colors(
                                selectedIconColor = InkBlack,
                                unselectedIconColor = InkBlack.copy(alpha = 0.4f),
                                selectedTextColor = InkBlack,
                                unselectedTextColor = InkBlack.copy(alpha = 0.4f),
                                indicatorColor = HoneyYellow.copy(alpha = 0.4f)
                            )
                        )
                    }
                }
            }
        ) { paddingValues ->
            NavHost(
                navController = bottomNavController,
                startDestination = Routes.Home.route,
                modifier = Modifier.padding(paddingValues)
            ) {
                composable(Routes.Home.route) {
                    DashboardScreen(
                        onNavigateToSessions = { bottomNavController.navigate(Routes.Sessions.route) },
                        onNavigateToGoals = { bottomNavController.navigate(Routes.Goals.route) },
                        onNavigateToAddHobby = { parentNavController.navigate(Routes.AddHobby.route) },
                        onNavigateToAnalytics = { bottomNavController.navigate(Routes.Analytics.route) },
                        onNavigateToProfile = { parentNavController.navigate(Routes.Profile.route) },
                        onNavigateToHobbyDetail = { id -> parentNavController.navigate(Routes.HobbyDetail.createRoute(id)) },
                        onOpenDrawer = { scope.launch { drawerState.open() } }
                    )
                }
                composable(Routes.Sessions.route) { SessionsScreen() }
                composable(Routes.Goals.route) { GoalsScreen() }
                composable(Routes.Planner.route) { StudyPlannerScreen() }
                composable(Routes.Analytics.route) { 
                    AnalyticsScreen(
                        onNavigateToLeaderboard = { parentNavController.navigate(Routes.Leaderboard.route) }
                    ) 
                }
                composable(Routes.Profile.route) {
                    ProfileScreen(
                        userPreferencesRepository = userPreferencesRepository,
                        userRepository = userRepository,
                        hobbyRepository = hobbyRepository,
                        onNavigateBack = { bottomNavController.navigateUp() },
                        onNavigateToSettings = { parentNavController.navigate(Routes.Settings.route) },
                        onNavigateToAchievements = { parentNavController.navigate(Routes.Achievements.route) }
                    )
                }
            }
        }
    }
}

data class BottomNavItem(val route: String, val title: String, val icon: androidx.compose.ui.graphics.vector.ImageVector)
