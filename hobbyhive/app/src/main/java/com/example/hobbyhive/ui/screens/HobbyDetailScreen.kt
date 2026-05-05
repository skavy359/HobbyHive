package com.example.hobbyhive.ui.screens

import android.content.Intent
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.model.HobbyStatus
import com.example.hobbyhive.ui.components.BadgeChip
import com.example.hobbyhive.ui.components.GradientPageHeader
import com.example.hobbyhive.ui.components.GradientProgressBar
import com.example.hobbyhive.ui.components.SectionHeader
import com.example.hobbyhive.ui.theme.*
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HobbyDetailScreen(
    hobbyId: Long,
    hobbyRepository: HobbyRepository,
    onNavigateBack: () -> Unit,
    onEditHobby: (Long) -> Unit
) {
    val hobby by hobbyRepository.getHobbyById(hobbyId).collectAsState(initial = null)
    val context = LocalContext.current
    val scope = rememberCoroutineScope()
    var showDeleteDialog by remember { mutableStateOf(false) }

    if (showDeleteDialog) {
        AlertDialog(
            onDismissRequest = { showDeleteDialog = false },
            title = { Text("Delete Hobby?", fontWeight = FontWeight.Bold) },
            text = { Text("This action cannot be undone. All data will be removed.") },
            confirmButton = {
                TextButton(onClick = {
                    scope.launch { hobbyRepository.deleteHobbyById(hobbyId); showDeleteDialog = false; onNavigateBack() }
                }) { Text("Delete", color = MaterialTheme.colorScheme.error, fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showDeleteDialog = false }) { Text("Cancel") } }
        )
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } },
                actions = {
                    IconButton(onClick = { onEditHobby(hobbyId) }) { Icon(Icons.Default.Edit, "Edit") }
                    IconButton(onClick = { showDeleteDialog = true }) { Icon(Icons.Default.Delete, "Delete", tint = ErrorRed) }
                }
            )
        }
    ) { padding ->
        LazyColumn(
            modifier = Modifier.fillMaxSize().padding(padding).padding(horizontal = 16.dp),
            contentPadding = PaddingValues(bottom = 24.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            hobby?.let { h ->
                item {
                    val statusColor = when (h.status) { HobbyStatus.ACTIVE -> SuccessGreen; HobbyStatus.COMPLETED -> ThemeIndigo; HobbyStatus.ARCHIVED -> MaterialTheme.colorScheme.onSurfaceVariant }
                    GradientPageHeader(
                        title = h.name, subtitle = h.description.ifBlank { "No description provided." },
                        badgeIcon = Icons.Default.Category, badgeText = h.category.displayName,
                        gradientColors = listOf(SessionTeal, SessionCyan),
                        actionButton = {
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                BadgeChip(h.status.displayName, statusColor)
                                BadgeChip("⭐ ${String.format("%.1f", h.rating)}", AccentAmber)
                            }
                        }
                    )
                }

                if (h.imageUri != null) {
                    item {
                        Card(shape = RoundedCornerShape(24.dp), modifier = Modifier.fillMaxWidth().height(200.dp)) {
                            AsyncImage(model = h.imageUri, contentDescription = h.name, modifier = Modifier.fillMaxSize(), contentScale = ContentScale.Crop)
                        }
                    }
                }

                item { SectionHeader("Progress Overview") }
                item {
                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.padding(20.dp)) {
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Mastery Level", fontWeight = FontWeight.Bold, fontSize = 15.sp)
                                Text("${h.progress}%", fontWeight = FontWeight.Black, fontSize = 15.sp, color = SessionTeal)
                            }
                            Spacer(Modifier.height(12.dp))
                            GradientProgressBar(progress = h.progress / 100f, colors = listOf(SessionGreen, SessionTeal, SessionCyan))
                        }
                    }
                }

                if (h.notes.isNotBlank()) {
                    item { SectionHeader("Notes") }
                    item {
                        Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface), modifier = Modifier.fillMaxWidth()) {
                            Text(h.notes, modifier = Modifier.padding(20.dp), fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurface, lineHeight = 20.sp)
                        }
                    }
                }

                item { SectionHeader("Details") }
                item {
                    Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface)) {
                        Column(Modifier.padding(20.dp)) {
                            val df = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Created On", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(df.format(Date(h.createdAt)), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                            Spacer(Modifier.height(12.dp))
                            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                                Text("Last Updated", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                Text(df.format(Date(h.updatedAt)), fontWeight = FontWeight.Bold, fontSize = 14.sp)
                            }
                        }
                    }
                }
            } ?: item { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
        }
    }
}
