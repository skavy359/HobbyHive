package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.background
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
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.ui.draw.alpha
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hobbyhive.ui.components.*
import com.example.hobbyhive.ui.theme.*
import com.example.hobbyhive.viewmodel.LeaderboardViewModel
import com.example.hobbyhive.viewmodel.LeaderboardEntry

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LeaderboardScreen(
    onNavigateBack: () -> Unit = {},
    viewModel: LeaderboardViewModel = viewModel()
) {
    val state by viewModel.uiState.collectAsState()
    var activeTab by remember { mutableStateOf(0) } // 0: Time, 1: Sessions

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
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 16.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
        item {
            GradientPageHeader(
                title = "Leaderboard", subtitle = "See which hobbies you spend the most time on.",
                badgeIcon = Icons.Default.EmojiEvents, badgeText = "Global Rankings",
                gradientColors = listOf(LeaderAmber, LeaderRose)
            )
        }

        item {
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                indicator = { },
                divider = { }
            ) {
                listOf("Top Hours", "Most Sessions").forEachIndexed { index, text ->
                    val isSelected = activeTab == index
                    val bg = if (isSelected) Brush.horizontalGradient(listOf(LeaderAmber, LeaderRose)) else Brush.horizontalGradient(listOf(Color.Transparent, Color.Transparent))
                    val txtColor = if (isSelected) Color.White else MaterialTheme.colorScheme.onSurfaceVariant
                    Box(
                        modifier = Modifier
                            .padding(horizontal = 4.dp)
                            .clip(RoundedCornerShape(16.dp))
                            .background(bg)
                    ) {
                        Tab(
                            selected = isSelected,
                            onClick = { activeTab = index },
                            text = { Text(text, color = txtColor, fontWeight = FontWeight.Bold) },
                        )
                    }
                }
            }
        }

        val data = if (activeTab == 0) state.topTimeHobbies else state.topSessionHobbies

        if (data.isEmpty() && !state.isLoading) {
            item {
                EmptyState(
                    icon = Icons.Default.StarBorder,
                    title = "No Rankings Yet",
                    subtitle = "Log sessions to see your top hobbies appear here."
                )
            }
        }

        // Podium for top 3
        if (data.size >= 3) {
            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(vertical = 16.dp),
                    horizontalArrangement = Arrangement.Center,
                    verticalAlignment = Alignment.Bottom
                ) {
                    // 2nd Place
                    PodiumColumn(data[1], 2, LeaderOrange, 100.dp)
                    // 1st Place
                    PodiumColumn(data[0], 1, LeaderAmber, 130.dp, true)
                    // 3rd Place
                    PodiumColumn(data[2], 3, LeaderRose, 80.dp)
                }
            }
        }

        // Full rankings table
        if (data.isNotEmpty()) {
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)
                ) {
                    Column(Modifier.padding(vertical = 8.dp)) {
                        Row(Modifier.fillMaxWidth().padding(horizontal = 20.dp, vertical = 12.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.SpaceBetween) {
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Icon(Icons.Default.Adjust, null, tint = LeaderAmber, modifier = Modifier.size(20.dp))
                                Text("Full Rankings", fontWeight = FontWeight.Black, fontSize = 16.sp)
                            }
                            Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(8.dp)) {
                                Text("${data.size} Hobbies", Modifier.padding(horizontal = 10.dp, vertical = 4.dp), fontSize = 11.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                        
                        data.forEachIndexed { i, entry ->
                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .padding(horizontal = 20.dp, vertical = 12.dp),
                                verticalAlignment = Alignment.CenterVertically,
                                horizontalArrangement = Arrangement.spacedBy(16.dp)
                            ) {
                                Text(
                                    text = when (entry.rank) { 1 -> "🥇"; 2 -> "🥈"; 3 -> "🥉"; else -> "#${entry.rank}" },
                                    fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant, modifier = Modifier.width(30.dp), textAlign = TextAlign.Center
                                )
                                Surface(modifier = Modifier.size(48.dp), shape = RoundedCornerShape(14.dp), color = MaterialTheme.colorScheme.surfaceVariant) {
                                    Box(contentAlignment = Alignment.Center) { Text(entry.emoji, fontSize = 24.sp) }
                                }
                                Text(entry.hobbyName, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                                Surface(color = MaterialTheme.colorScheme.surfaceVariant, shape = RoundedCornerShape(12.dp)) {
                                    Row(Modifier.padding(horizontal = 12.dp, vertical = 8.dp), verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                        Text("${entry.value}", fontWeight = FontWeight.Black, fontSize = 16.sp)
                                        Text(entry.metric.uppercase(), fontSize = 10.sp, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    }
                                }
                            }
                            if (i < data.lastIndex) HorizontalDivider(color = MaterialTheme.colorScheme.surfaceVariant, modifier = Modifier.padding(start = 70.dp, end = 20.dp))
                        }
                    }
                }
            }
        }
    }
}
}

@Composable
fun PodiumColumn(entry: LeaderboardEntry, rank: Int, color: Color, height: androidx.compose.ui.unit.Dp, isFirst: Boolean = false) {
    Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(horizontal = 8.dp)) {
        if (isFirst) {
            Icon(Icons.Default.WorkspacePremium, null, tint = color, modifier = Modifier.size(32.dp).padding(bottom = 8.dp))
        }
        Surface(modifier = Modifier.size(if (isFirst) 64.dp else 56.dp), shape = CircleShape, color = color.copy(alpha = 0.2f), border = androidx.compose.foundation.BorderStroke(2.dp, color)) {
            Box(contentAlignment = Alignment.Center) { Text(entry.emoji, fontSize = if (isFirst) 32.sp else 26.sp) }
        }
        Spacer(Modifier.height(8.dp))
        Surface(modifier = Modifier.width(80.dp).height(height), shape = RoundedCornerShape(topStart = 16.dp, topEnd = 16.dp), color = color) {
            Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.padding(top = 16.dp)) {
                Text("$rank", color = Color.White, fontSize = 32.sp, fontWeight = FontWeight.Black, modifier = Modifier.alpha(0.8f))
                Surface(color = Color.Black.copy(alpha = 0.2f), shape = RoundedCornerShape(50), modifier = Modifier.padding(top = 8.dp)) {
                    Text("${entry.value}", color = Color.White, fontSize = 12.sp, fontWeight = FontWeight.Bold, modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp))
                }
            }
        }
    }
}
