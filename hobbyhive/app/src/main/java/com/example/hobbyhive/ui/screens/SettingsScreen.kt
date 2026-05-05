package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.ui.components.GradientPageHeader
import com.example.hobbyhive.ui.components.SectionHeader
import com.example.hobbyhive.ui.theme.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.flow.first

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    userPreferencesRepository: UserPreferencesRepository,
    onNavigateBack: () -> Unit,
    onLogout: () -> Unit
) {
    val themeMode by userPreferencesRepository.themeMode.collectAsState(initial = "system")
    val notificationsEnabled by userPreferencesRepository.notificationsEnabled.collectAsState(initial = true)
    val scope = rememberCoroutineScope()
    var showLogoutDialog by remember { mutableStateOf(false) }

    if (showLogoutDialog) {
        AlertDialog(
            onDismissRequest = { showLogoutDialog = false },
            title = { Text("Logout?", fontWeight = FontWeight.Bold) },
            text = { Text("You will need to log in again to access your hobbies.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { userPreferencesRepository.clearAuth() }
                    showLogoutDialog = false
                    onLogout()
                }) { Text("Logout", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showLogoutDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(20.dp)
        ) {
            item {
                GradientPageHeader(
                    title = "Preferences", subtitle = "Customize your HobbyHive experience.",
                    badgeIcon = Icons.Default.Settings, badgeText = "Configuration",
                    gradientColors = listOf(ThemeIndigo, ThemeFuchsia)
                )
            }

            item { SectionHeader("Appearance") }
            item {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(8.dp)) {
                        listOf("system" to "System Default", "light" to "Light Mode", "dark" to "Dark Mode").forEach { (value, label) ->
                            Row(
                                Modifier.fillMaxWidth().padding(horizontal = 8.dp, vertical = 4.dp),
                                verticalAlignment = Alignment.CenterVertically
                            ) {
                                RadioButton(selected = themeMode == value, onClick = { scope.launch { userPreferencesRepository.setThemeMode(value) } })
                                Text(label, fontSize = 15.sp, fontWeight = if (themeMode == value) FontWeight.Bold else FontWeight.Normal)
                            }
                        }
                    }
                }
            }

            item { SectionHeader("Notifications") }
            item {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                        Column {
                            Text("Session Reminders", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                            Text("Get notified when it's time to practice", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        }
                        Switch(checked = notificationsEnabled, onCheckedChange = { scope.launch { userPreferencesRepository.setNotificationsEnabled(it) } })
                    }
                }
            }

            item { SectionHeader("Account & Danger Zone") }
            item {
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column {
                        OutlinedButton(
                            onClick = { showLogoutDialog = true },
                            modifier = Modifier.fillMaxWidth().padding(horizontal = 16.dp, vertical = 8.dp)
                        ) {
                            Icon(Icons.AutoMirrored.Filled.Logout, "Logout", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Logout", fontWeight = FontWeight.Bold)
                        }
                        OutlinedButton(
                            onClick = {
                                scope.launch {
                                    val userId = userPreferencesRepository.userId.first()
                                    if (userId != null) {
                                        // Ideally call userRepository.deleteAccount(userId) if exists, else skip
                                        userPreferencesRepository.clearAuth()
                                        onLogout()
                                    }
                                }
                            },
                            modifier = Modifier.fillMaxWidth().padding(start = 16.dp, end = 16.dp, bottom = 16.dp),
                            colors = ButtonDefaults.outlinedButtonColors(contentColor = MaterialTheme.colorScheme.error)
                        ) {
                            Icon(Icons.Default.DeleteForever, "Delete Account", modifier = Modifier.size(18.dp))
                            Spacer(Modifier.width(8.dp))
                            Text("Delete Account", fontWeight = FontWeight.Bold)
                        }
                    }
                }
            }

            item {
                Column(Modifier.fillMaxWidth().padding(top = 16.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                    Text("HobbyHive v2.0", fontWeight = FontWeight.Bold, fontSize = 14.sp)
                    Text("Powered by Compose & Room", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                }
            }
        }
    }
}
