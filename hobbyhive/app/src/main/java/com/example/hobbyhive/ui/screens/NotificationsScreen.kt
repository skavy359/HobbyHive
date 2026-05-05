package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Notifications
import androidx.compose.material.icons.filled.NotificationsOff
import androidx.compose.material.icons.filled.NotificationsActive
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.ui.components.EmptyState
import com.example.hobbyhive.ui.components.GradientPageHeader
import com.example.hobbyhive.ui.theme.*
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NotificationsScreen(
    hobbyRepository: HobbyRepository,
    onNavigateBack: () -> Unit
) {
    val hobbies by hobbyRepository.getAllHobbies().collectAsState(initial = emptyList())
    val scope = rememberCoroutineScope()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            item {
                GradientPageHeader(
                    title = "Reminders", subtitle = "Manage alerts for your hobbies.",
                    badgeIcon = Icons.Default.NotificationsActive, badgeText = "Notifications",
                    gradientColors = listOf(ThemeOrange, ThemeRose)
                )
            }

            if (hobbies.isEmpty()) {
                item {
                    EmptyState(Icons.Default.NotificationsOff, "No Reminders", "Add a hobby first to configure alerts.")
                }
            } else {
                items(hobbies, key = { it.id }) { hobby ->
                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
                            Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(14.dp), color = ThemeOrange.copy(alpha = 0.1f)) {
                                Box(contentAlignment = Alignment.Center) { Text(hobby.category.emoji, fontSize = 24.sp) }
                            }
                            Spacer(Modifier.width(16.dp))
                            Column(Modifier.weight(1f)) {
                                Text(hobby.name, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                Text(if (hobby.reminderEnabled) "Reminder active" else "Alerts paused", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            Switch(
                                checked = hobby.reminderEnabled,
                                onCheckedChange = { isEnabled ->
                                    scope.launch {
                                        hobbyRepository.updateHobby(hobby.copy(reminderEnabled = isEnabled))
                                    }
                                },
                                colors = SwitchDefaults.colors(
                                    checkedThumbColor = ThemeOrange,
                                    checkedTrackColor = ThemeOrange.copy(alpha = 0.5f)
                                )
                            )
                        }
                    }
                }
            }
        }
    }
}
