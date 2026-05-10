package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hobbyhive.ui.components.*
import com.example.hobbyhive.ui.theme.*
import com.example.hobbyhive.viewmodel.AnalyticsViewModel

@Composable
fun AnalyticsScreen(
    onNavigateToLeaderboard: () -> Unit = {},
    viewModel: AnalyticsViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GradientPageHeader(
                title = "Analytics", subtitle = "Insights into your learning patterns and habits.",
                badgeIcon = Icons.Default.Analytics, badgeText = "Learning Health",
                gradientColors = listOf(AnalyticsPurple, AnalyticsFuchsia),
                actionButton = {
                    Button(onClick = onNavigateToLeaderboard, colors = ButtonDefaults.buttonColors(containerColor = PaperWhite, contentColor = InkBlack), border = BorderStroke(2.dp, InkBlack)) {
                        Icon(Icons.Default.EmojiEvents, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Leaderboard", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        // Engagement + Stats
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                // Engagement Ring
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack), modifier = Modifier.weight(1f)) {
                    Column(Modifier.padding(20.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("Engagement", fontWeight = FontWeight.Bold, fontSize = 13.sp, color = Charcoal)
                        Spacer(Modifier.height(12.dp))
                        RingChart(progress = state.engagementScore, size = 100.dp, strokeWidth = 10.dp, color = AnalyticsPurple, label = "score")
                    }
                }
                Column(Modifier.weight(1f), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    GradientStatCard("HOURS", "${state.totalHoursLogged}", Icons.Default.AccessTime, listOf(AnalyticsPurple, AnalyticsFuchsia))
                    GradientStatCard("SESSIONS", "${state.totalSessions}", Icons.Default.EventNote, listOf(GoalIndigo, GoalViolet))
                }
            }
        }

        // Streak Card
        item {
            Box(Modifier.fillMaxWidth().clip(RoundedCornerShape(24.dp)).background(Brush.horizontalGradient(listOf(StreakOrange, StreakRose))).padding(20.dp)) {
                Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                    Column {
                        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            Icon(Icons.Default.LocalFireDepartment, null, tint = Color.White, modifier = Modifier.size(28.dp))
                            Text("Current Streak", color = Color.White.copy(alpha = 0.8f), fontWeight = FontWeight.SemiBold, fontSize = 14.sp)
                        }
                        Text("${state.currentStreak} days", color = Color.White, fontSize = 36.sp, fontWeight = FontWeight.Black)
                    }
                    Surface(color = Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(12.dp)) {
                        Text("Best: ${state.longestStreak}", Modifier.padding(horizontal = 12.dp, vertical = 6.dp), color = Color.White, fontWeight = FontWeight.Bold, fontSize = 12.sp)
                    }
                }
            }
        }

        // Burnout indicator
        item {
            val burnColor = when (state.burnoutRisk) { "High" -> ErrorRed; "Moderate" -> AccentAmber; else -> SuccessGreen }
            Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack)) {
                Row(Modifier.fillMaxWidth().padding(16.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    Surface(Modifier.size(48.dp), shape = RoundedCornerShape(14.dp), color = burnColor.copy(alpha = 0.1f)) {
                        Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.MonitorHeart, null, tint = burnColor) }
                    }
                    Column(Modifier.weight(1f)) {
                        Text("Burnout Risk", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                        Text(when (state.burnoutRisk) { "High" -> "Consider taking a rest day"; "Moderate" -> "Good pace, watch for overwork"; else -> "Healthy balance!" }, fontSize = 12.sp, color = Charcoal)
                    }
                    BadgeChip(state.burnoutRisk, burnColor)
                }
            }
        }

        // Weekly chart
        if (state.weeklyHours.isNotEmpty()) {
            item {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack)) {
                    Column(Modifier.padding(20.dp)) {
                        SectionHeader("Weekly Activity")
                        Spacer(Modifier.height(16.dp))
                        val items = state.weeklyHours.mapIndexed { i, (day, hrs) ->
                            val colors = listOf(GoalIndigo, AnalyticsPurple, SessionGreen, AccentAmber, StreakOrange, StreakRose, AccentTeal)
                            BarChartItem(day, hrs, colors[i % colors.size])
                        }
                        BarChart(items = items)
                    }
                }
            }
        }

        // Top hobbies
        if (state.topHobbies.isNotEmpty()) {
            item {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack)) {
                    Column(Modifier.padding(20.dp)) {
                        SectionHeader("Top Hobbies by Time")
                        Spacer(Modifier.height(12.dp))
                        state.topHobbies.forEachIndexed { i, entry ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 8.dp), verticalAlignment = Alignment.CenterVertically) {
                                Text("${i + 1}", fontWeight = FontWeight.Black, fontSize = 18.sp, color = Charcoal, modifier = Modifier.width(30.dp))
                                Text(entry.emoji, fontSize = 20.sp)
                                Spacer(Modifier.width(8.dp))
                                Text(entry.hobbyName, fontWeight = FontWeight.Bold, fontSize = 14.sp, modifier = Modifier.weight(1f))
                                Text("${entry.totalMinutes / 60}h ${entry.totalMinutes % 60}m", fontWeight = FontWeight.SemiBold, fontSize = 13.sp, color = AnalyticsPurple)
                            }
                            if (i < state.topHobbies.lastIndex) HorizontalDivider(Modifier.padding(start = 38.dp))
                        }
                    }
                }
            }
        }

        // Insights
        if (state.insights.isNotEmpty()) {
            item {
                Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack)) {
                    Column(Modifier.padding(20.dp)) {
                        SectionHeader("💡 Insights")
                        Spacer(Modifier.height(12.dp))
                        state.insights.forEach { insight ->
                            Row(Modifier.fillMaxWidth().padding(vertical = 6.dp), horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Text("•", fontWeight = FontWeight.Bold, color = AnalyticsPurple)
                                Text(insight, fontSize = 13.sp, color = Charcoal, lineHeight = 18.sp)
                            }
                        }
                    }
                }
            }
        }
    }
}
