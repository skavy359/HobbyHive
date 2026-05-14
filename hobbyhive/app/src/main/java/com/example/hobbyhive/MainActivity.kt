package com.example.hobbyhive

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.example.hobbyhive.data.HobbyHiveDatabase
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.data.UserRepository
import com.example.hobbyhive.ui.navigation.NavGraph
import com.example.hobbyhive.ui.theme.HobbyhiveTheme
import com.example.hobbyhive.util.RequestNotificationPermission

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Android 12+ Splash Screen API
        val splashScreen = installSplashScreen()

        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // Initialize dependencies
        val database = HobbyHiveDatabase.getDatabase(applicationContext)
        val userPreferencesRepository = UserPreferencesRepository(applicationContext)
        
        // Initialize Appwrite repositories
        val appwriteAuthRepository = com.example.hobbyhive.appwrite.repository.AppwriteAuthRepository()
        val appwriteUserRepository = com.example.hobbyhive.appwrite.repository.AppwriteProfileRepository(
            database.userDao(),
            userPreferencesRepository
        )
        val appwriteHobbyRepository = com.example.hobbyhive.appwrite.repository.AppwriteHobbyRepository(
            database.hobbyDao(),
            userPreferencesRepository
        )

        // Keep splash screen visible briefly
        var keepSplashVisible = true
        splashScreen.setKeepOnScreenCondition { keepSplashVisible }

        setContent {
            // Read theme preference
            val themeMode by userPreferencesRepository.themeMode.collectAsState(initial = "system")
            val isDarkTheme = when (themeMode) {
                "dark" -> true
                "light" -> false
                else -> androidx.compose.foundation.isSystemInDarkTheme()
            }

            // Dismiss system splash once Compose is ready
            LaunchedEffect(Unit) {
                keepSplashVisible = false
            }

            HobbyhiveTheme(darkTheme = isDarkTheme) {
                // Request notification permission on first launch
                RequestNotificationPermission()

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(
                        navController = navController,
                        userPreferencesRepository = userPreferencesRepository,
                        userRepository = appwriteUserRepository,
                        appwriteAuthRepository = appwriteAuthRepository,
                        hobbyRepository = appwriteHobbyRepository
                    )
                }
            }
        }
    }
}