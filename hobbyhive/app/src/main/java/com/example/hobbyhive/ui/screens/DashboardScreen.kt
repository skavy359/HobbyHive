package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
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
        // Greeting Header — Sticker style
        item {
            Card(
                shape = RoundedCornerShape(22.dp),
                colors = CardDefaults.cardColors(containerColor = HoneyYellow.copy(alpha = 0.35f)),
                border = BorderStroke(2.5.dp, InkBlack),
                modifier = Modifier.fillMaxWidth().clickable { onNavigateToProfile() }
            ) {
                Column(Modifier.padding(16.dp)) {
                    Row(verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = onOpenDrawer, modifier = Modifier.size(32.dp).offset(x = (-8).dp)) {
                            Icon(Icons.Default.Menu, contentDescription = "Menu", tint = InkBlack)
                        }
                        Text(
                            "${viewModel.getGreeting()}, ${state.userName.split(" ").first()}! ${viewModel.getGreetingEmoji()}",
                            color = InkBlack,
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Black
                        )
                    }
                    Spacer(Modifier.height(2.dp))
                    Text(
                        "Track your hobbies and build consistency.",
                        color = Charcoal,
                        fontSize = 12.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                }
            }
        }

        // Stat Cards
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientStatCard("TOTAL HOBBIES", "${state.totalHobbies}", Icons.Default.Category, listOf(PastelLavender), Modifier.weight(1f))
                GradientStatCard("ACTIVE", "${state.activeHobbies}", Icons.Default.PlayArrow, listOf(LimeGreen), Modifier.weight(1f))
            }
        }
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientStatCard("🔥 STREAK", "${state.currentStreak}d", Icons.Default.LocalFireDepartment, listOf(HoneyAmber), Modifier.weight(1f))
                GradientStatCard("THIS WEEK", "${state.weeklyMinutes / 60}h ${state.weeklyMinutes % 60}m", Icons.Default.AccessTime, listOf(CyanSky), Modifier.weight(1f))
            }
        }

        // Honeycomb Heatmap
        if (state.dailyMinutesMap.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(22.dp),
                    colors = CardDefaults.cardColors(containerColor = PaperWhite),
                    border = BorderStroke(2.5.dp, InkBlack)
                ) {
                    Column(Modifier.padding(18.dp)) {
                        SectionHeader("Honeycomb Activity", actionLabel = "Analytics", onAction = onNavigateToAnalytics)
                        Spacer(Modifier.height(14.dp))
                        Row(Modifier.fillMaxWidth().horizontalScroll(rememberScrollState())) {
                            ActivityHeatmap(dailyMinutes = state.dailyMinutesMap, baseColor = HoneyYellow)
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
                    Card(
                        shape = RoundedCornerShape(18.dp),
                        colors = CardDefaults.cardColors(containerColor = PaperWhite),
                        border = BorderStroke(2.dp, InkBlack),
                        modifier = Modifier.fillMaxWidth().clickable { onNavigateToHobbyDetail(hobby.id) }
                    ) {
                        Row(Modifier.padding(14.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                            Surface(
                                Modifier.size(46.dp),
                                shape = RoundedCornerShape(14.dp),
                                color = HoneyLight,
                                border = BorderStroke(1.5.dp, InkBlack)
                            ) {
                                Box(contentAlignment = Alignment.Center) { Text(hobby.category.emoji, fontSize = 22.sp) }
                            }
                            Column(Modifier.weight(1f)) {
                                Text(hobby.name, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = InkBlack)
                                Text(hobby.category.displayName, fontSize = 12.sp, color = Charcoal, fontWeight = FontWeight.SemiBold)
                            }
                            BadgeChip(hobby.status.displayName, if (hobby.status.displayName == "Active") LimeGreen else HoneyAmber)
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
                    TimelineItem(accentColor = HoneyYellow, isLast = idx == state.recentSessions.lastIndex) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column {
                                Text(entry.hobbyName, fontWeight = FontWeight.ExtraBold, fontSize = 14.sp, color = InkBlack)
                                Text(df.format(Date(entry.session.sessionDate)), fontSize = 12.sp, color = Charcoal)
                            }
                            BadgeChip("${entry.session.durationMinutes}m", LimeGreen)
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
