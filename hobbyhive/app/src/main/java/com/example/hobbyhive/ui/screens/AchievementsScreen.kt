package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.GridItemSpan
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hobbyhive.data.AchievementData
import com.example.hobbyhive.model.Achievement
import com.example.hobbyhive.ui.components.GradientProgressBar
import com.example.hobbyhive.ui.theme.*
import com.example.hobbyhive.viewmodel.DashboardViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AchievementsScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: DashboardViewModel = viewModel() // Reusing to get counts/streaks for now
) {
    val state by viewModel.uiState.collectAsState()
    
    // Seed data computation based on real stats
    val achievements = AchievementData.getAchievements(
        currentStreak = state.currentStreak,
        longestStreak = state.currentStreak, // Simplification for now
        totalHobbies = state.totalHobbies,
        completedHobbies = 0, // Simplification
        totalHoursLogged = state.weeklyMinutes / 60, // Simplification
        totalGoals = 0, // Simplification
        totalSessions = state.recentSessions.size // Simplification
    )

    val unlockedCount = achievements.count { it.isUnlocked }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) { Icon(Icons.Default.ArrowBack, "Back") }
                }
            )
        }
    ) { padding ->
        LazyVerticalGrid(
            columns = GridCells.Adaptive(minSize = 300.dp),
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            horizontalArrangement = Arrangement.spacedBy(16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item(span = { GridItemSpan(maxLineSpan) }) {
                com.example.hobbyhive.ui.components.GradientPageHeader(
                    title = "Trophy Cabinet", subtitle = "Unlock badges by hitting milestones.",
                    badgeIcon = Icons.Default.EmojiEvents, badgeText = "$unlockedCount / ${achievements.size} Unlocked",
                    gradientColors = listOf(ThemeFuchsia, ThemeRose)
                )
            }

        items(achievements) { achievement ->
            AchievementCard(achievement)
        }
    }
}
}

@Composable
fun AchievementCard(achievement: Achievement) {
    val color = when (achievement.theme) {
        "orange" -> ThemeOrange; "indigo" -> ThemeIndigo; "emerald" -> ThemeEmerald
        "fuchsia" -> ThemeFuchsia; "blue" -> ThemeBlue; "rose" -> ThemeRose
        "cyan" -> ThemeCyan; "amber" -> ThemeAmberAch; else -> MaterialTheme.colorScheme.primary
    }

    val iconVector: ImageVector = when (achievement.icon) {
        "flame" -> Icons.Default.LocalFireDepartment
        "lightbulb" -> Icons.Default.EmojiObjects
        "clock" -> Icons.Default.AccessTime
        "target" -> Icons.Default.GpsFixed
        "award" -> Icons.Default.WorkspacePremium
        "activity" -> Icons.Default.ShowChart
        "crown" -> Icons.Default.EmojiEvents
        else -> Icons.Default.Star
    }

    Card(
        shape = RoundedCornerShape(24.dp),
        colors = CardDefaults.cardColors(
            containerColor = if (achievement.isUnlocked) color.copy(alpha = 0.05f) else MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
        ),
        border = androidx.compose.foundation.BorderStroke(1.dp, if (achievement.isUnlocked) color.copy(alpha = 0.3f) else MaterialTheme.colorScheme.surfaceVariant),
        modifier = Modifier.fillMaxWidth().alpha(if (achievement.isUnlocked) 1f else 0.6f)
    ) {
        Row(Modifier.padding(20.dp), verticalAlignment = Alignment.Top, horizontalArrangement = Arrangement.spacedBy(16.dp)) {
            Surface(
                modifier = Modifier.size(56.dp),
                shape = RoundedCornerShape(16.dp),
                color = if (achievement.isUnlocked) color.copy(alpha = 0.1f) else MaterialTheme.colorScheme.surfaceVariant
            ) {
                Box(contentAlignment = Alignment.Center) {
                    Icon(iconVector, null, tint = if (achievement.isUnlocked) color else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.size(28.dp))
                }
            }
            Column(Modifier.weight(1f)) {
                Text(achievement.title, fontWeight = FontWeight.Black, fontSize = 18.sp, color = if (achievement.isUnlocked) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant)
                Text(achievement.progress.uppercase(), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = if (achievement.isUnlocked) color else MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.padding(bottom = 4.dp))
                Text(achievement.description, fontSize = 13.sp, color = MaterialTheme.colorScheme.onSurfaceVariant, lineHeight = 18.sp)
                
                if (!achievement.isUnlocked && achievement.targetValue > 0) {
                    Spacer(Modifier.height(12.dp))
                    GradientProgressBar(progress = achievement.progressPercent / 100f, colors = listOf(color, color.copy(alpha = 0.7f)))
                }
            }
        }
    }
}
