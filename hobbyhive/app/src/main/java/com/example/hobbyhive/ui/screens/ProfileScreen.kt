package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.foundation.clickable
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.data.UserPreferencesRepository
import com.example.hobbyhive.data.UserRepository
import com.example.hobbyhive.model.HobbyStatus
import com.example.hobbyhive.ui.components.GradientPageHeader
import com.example.hobbyhive.ui.components.GradientStatCard
import com.example.hobbyhive.ui.components.SectionHeader
import com.example.hobbyhive.ui.theme.*
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProfileScreen(
    userPreferencesRepository: UserPreferencesRepository,
    userRepository: UserRepository,
    hobbyRepository: HobbyRepository,
    onNavigateBack: () -> Unit,
    onNavigateToSettings: () -> Unit = {},
    onNavigateToAchievements: () -> Unit = {}
) {
    val totalCount by hobbyRepository.getHobbyCount().collectAsState(initial = 0)
    val activeCount by hobbyRepository.getHobbyCountByStatus(HobbyStatus.ACTIVE).collectAsState(initial = 0)
    val completedCount by hobbyRepository.getHobbyCountByStatus(HobbyStatus.COMPLETED).collectAsState(initial = 0)
    
    var userName by remember { mutableStateOf("") }
    var userEmail by remember { mutableStateOf("") }
    var userAbout by remember { mutableStateOf("Lifelong learner and hobby enthusiast.") }
    var joinDate by remember { mutableStateOf("") }

    LaunchedEffect(Unit) {
        val userId = userPreferencesRepository.userId.first()
        if (userId != null) {
            val user = userRepository.getUserById(userId)
            if (user != null) {
                userName = user.fullName
                userEmail = user.email
                if (user.about.isNotBlank()) userAbout = user.about
                joinDate = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(Date(user.createdAt))
            }
        }
    }

    var showEditDialog by remember { mutableStateOf(false) }
    var editName by remember { mutableStateOf("") }
    var editAbout by remember { mutableStateOf("") }
    val scope = rememberCoroutineScope()

    if (showEditDialog) {
        AlertDialog(
            onDismissRequest = { showEditDialog = false },
            title = { Text("Edit Profile", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                    OutlinedTextField(
                        value = editName,
                        onValueChange = { editName = it },
                        label = { Text("Name") },
                        singleLine = true,
                        modifier = Modifier.fillMaxWidth()
                    )
                    OutlinedTextField(
                        value = editAbout,
                        onValueChange = { editAbout = it },
                        label = { Text("Tagline / About") },
                        maxLines = 3,
                        modifier = Modifier.fillMaxWidth()
                    )
                }
            },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch {
                        val userId = userPreferencesRepository.userId.first()
                        if (userId != null) {
                            userRepository.updateProfile(userId, editName.trim(), editAbout.trim())
                            userName = editName.trim()
                            userAbout = editAbout.trim()
                        }
                        showEditDialog = false
                    }
                }) { Text("Save", fontWeight = FontWeight.Bold) }
            },
            dismissButton = {
                TextButton(onClick = { showEditDialog = false }) { Text("Cancel") }
            }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Profile", fontWeight = FontWeight.Bold) },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                actions = { IconButton(onClick = onNavigateToSettings) { Icon(Icons.Default.Settings, "Settings") } }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            item {
                Card(
                    shape = RoundedCornerShape(24.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    modifier = Modifier.fillMaxWidth().padding(top = 8.dp)
                ) {
                    Column(Modifier.fillMaxWidth().padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                        Box {
                            Surface(
                                modifier = Modifier.size(100.dp),
                                shape = CircleShape,
                                color = ThemeIndigo.copy(alpha = 0.1f)
                            ) {
                                Box(contentAlignment = Alignment.Center) {
                                    Text(if (userName.isNotBlank()) userName.first().uppercase() else "?", fontSize = 40.sp, fontWeight = FontWeight.Black, color = ThemeIndigo)
                                }
                            }
                            Surface(
                                modifier = Modifier.align(Alignment.BottomEnd).offset(x = 4.dp, y = 4.dp).size(32.dp).clickable { 
                                    editName = userName; editAbout = userAbout; showEditDialog = true 
                                },
                                shape = CircleShape,
                                color = MaterialTheme.colorScheme.primary,
                                border = androidx.compose.foundation.BorderStroke(2.dp, MaterialTheme.colorScheme.surface)
                            ) {
                                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Edit, null, tint = Color.White, modifier = Modifier.size(16.dp)) }
                            }
                        }
                        Spacer(Modifier.height(16.dp))
                        Text(userName.ifBlank { "User" }, fontSize = 24.sp, fontWeight = FontWeight.Black)
                        Text(userEmail.ifBlank { "—" }, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                        
                        Spacer(Modifier.height(16.dp))
                        Surface(shape = RoundedCornerShape(12.dp), color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), modifier = Modifier.clickable { editName = userName; editAbout = userAbout; showEditDialog = true }) {
                            Text(userAbout, modifier = Modifier.padding(16.dp), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, textAlign = androidx.compose.ui.text.style.TextAlign.Center)
                        }
                        
                        if (joinDate.isNotBlank()) {
                            Spacer(Modifier.height(16.dp))
                            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(6.dp)) {
                                Icon(Icons.Default.CalendarToday, null, modifier = Modifier.size(14.dp), tint = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text("Joined $joinDate", fontSize = 12.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                            }
                        }
                    }
                }
            }

            item { SectionHeader("Statistics") }

            item {
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GradientStatCard("TOTAL HOBBIES", "$totalCount", Icons.Default.Category, listOf(GoalIndigo, GoalViolet), Modifier.weight(1f).fillMaxHeight())
                    GradientStatCard("ACTIVE", "$activeCount", Icons.Default.TrendingUp, listOf(SessionGreen, SessionCyan), Modifier.weight(1f).fillMaxHeight())
                }
            }
            item {
                Row(Modifier.fillMaxWidth().height(IntrinsicSize.Max), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                    GradientStatCard("COMPLETED", "$completedCount", Icons.Default.WorkspacePremium, listOf(StreakOrange, StreakRose), Modifier.weight(1f).fillMaxHeight())
                    // Achievement preview card
                    Card(
                        shape = RoundedCornerShape(24.dp),
                        colors = CardDefaults.cardColors(containerColor = ThemeFuchsia),
                        modifier = Modifier.weight(1f).fillMaxHeight().clickable { onNavigateToAchievements() }
                    ) {
                        Box(Modifier.fillMaxWidth().padding(20.dp)) {
                            Column(verticalArrangement = Arrangement.Center, modifier = Modifier.fillMaxHeight()) {
                                Text("TROPHIES", color = Color.White.copy(alpha = 0.85f), fontSize = 12.sp, fontWeight = FontWeight.Bold)
                                Spacer(Modifier.height(4.dp))
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Icon(Icons.Default.EmojiEvents, null, tint = Color.White, modifier = Modifier.size(28.dp))
                                    Text("View All", color = Color.White, fontSize = 16.sp, fontWeight = FontWeight.Bold)
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
