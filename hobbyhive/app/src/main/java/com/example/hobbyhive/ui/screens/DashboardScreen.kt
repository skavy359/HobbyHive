package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.clickable
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hobbyhive.ui.components.*
import com.example.hobbyhive.ui.theme.*
import com.example.hobbyhive.viewmodel.DashboardViewModel
import java.text.SimpleDateFormat
import java.util.*

@Composable
fun DashboardScreen(
    onNavigateToSessions: () -> Unit = {},
    onNavigateToGoals: () -> Unit = {},
    onNavigateToAddHobby: () -> Unit = {},
    onNavigateToAnalytics: () -> Unit = {},
    onNavigateToProfile: () -> Unit = {},
    onNavigateToHobbyDetail: (Long) -> Unit = {},
    onOpenDrawer: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 12.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        // Greeting Header
        item {
            Box(
                modifier = Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp))
                    .background(Brush.horizontalGradient(listOf(DashboardIndigo, DashboardPurple)))
                    .clickable { onNavigateToProfile() }
                    .padding(16.dp)
            ) {
                Box(Modifier.size(100.dp).align(Alignment.TopEnd).offset(x = 20.dp, y = (-20).dp).clip(CircleShape).background(Color.White.copy(alpha = 0.1f)).blur(30.dp))
                Column {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onOpenDrawer, modifier = Modifier.size(32.dp).offset(x = (-8).dp)) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = Color.White)
                        }
                        Text("${viewModel.getGreeting()}, ${state.userName.split(" ").first()}! ${viewModel.getGreetingEmoji()}", color = Color.White, fontSize = 20.sp, fontWeight = FontWeight.ExtraBold)
                    }
                    Spacer(Modifier.height(2.dp))
                    Text("Track your hobbies and build consistency.", color = Color.White.copy(alpha = 0.8f), fontSize = 12.sp, fontWeight = FontWeight.Medium)
                }
            }
        }

        // Stat Cards
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientStatCard("TOTAL HOBBIES", "${state.totalHobbies}", Icons.Default.Category, listOf(GoalIndigo, GoalViolet), Modifier.weight(1f))
                GradientStatCard("ACTIVE", "${state.activeHobbies}", Icons.Default.PlayArrow, listOf(SessionGreen, SessionCyan), Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientStatCard("🔥 STREAK", "${state.currentStreak}d", Icons.Default.LocalFireDepartment, listOf(StreakOrange, StreakRose), Modifier.weight(1f))
                GradientStatCard("THIS WEEK", "${state.weeklyMinutes / 60}h ${state.weeklyMinutes % 60}m", Icons.Default.AccessTime, listOf(AnalyticsPurple, AnalyticsFuchsia), Modifier.weight(1f))
            }
        }

        // Heatmap
        if (state.dailyMinutesMap.isNotEmpty()) {
            item {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                    Column(Modifier.padding(20.dp)) {
                        SectionHeader("Activity Heatmap", actionLabel = "Analytics", onAction = onNavigateToAnalytics)
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            ActivityHeatmap(dailyMinutes = state.dailyMinutesMap, baseColor = AccentPurple)
                        }
                    }
                }
            }
        }

        // Active Hobbies
        if (state.activeHobbyList.isNotEmpty()) {
            item {
                SectionHeader("Active Hobbies", actionLabel = "View All", onAction = {})
                Spacer(Modifier.height(8.dp))
            }
            state.activeHobbyList.forEach { hobby ->
                item {
                    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth().clickable { onNavigateToHobbyDetail(hobby.id) }) {
                        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(Modifier.size(48.dp), shape = RoundedCornerShape(14.dp), color = AccentPurple.copy(alpha = 0.1f)) {
                                Box(contentAlignment = Alignment.Center) { Text(hobby.category.emoji, fontSize = 22.sp) }
                            }
                            Column(Modifier.weight(1f)) {
                                Text(hobby.name, fontWeight = FontWeight.Bold, fontSize = 15.sp, color = MaterialTheme.colorScheme.onSurface)
                                Text(hobby.category.displayName, fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            BadgeChip(hobby.status.displayName, if (hobby.status.displayName == "Active") SuccessGreen else AccentAmber)
                        }
                    }
                }
            }
        }

        // Recent Sessions
        if (state.recentSessions.isNotEmpty()) {
            item {
                SectionHeader("Recent Sessions", actionLabel = "View All", onAction = onNavigateToSessions)
                Spacer(Modifier.height(8.dp))
            }
            state.recentSessions.forEachIndexed { idx, entry ->
                item {
                    val df = SimpleDateFormat("MMM dd", Locale.getDefault())
                    TimelineItem(accentColor = SessionGreen, isLast = idx == state.recentSessions.lastIndex) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(entry.hobbyName, fontWeight = FontWeight.Bold, fontSize = 14.sp)
                                Text(df.format(Date(entry.session.sessionDate)), fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                            BadgeChip("${entry.session.durationMinutes}m", SessionGreen)
                        }
                    }
                }
            }
        }

        // Empty state
        if (state.totalHobbies == 0 && !state.isLoading) {
            item {
                EmptyState(
                    icon = Icons.Default.Explore,
                    title = "Welcome to HobbyHive!",
                    subtitle = "Start by adding your first hobby to track your progress.",
                    actionLabel = "Add Hobby",
                    onAction = onNavigateToAddHobby
                )
            }
        }
    }
}
