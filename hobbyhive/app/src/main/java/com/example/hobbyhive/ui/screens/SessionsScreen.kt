package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hobbyhive.ui.components.*
import com.example.hobbyhive.ui.theme.*
import com.example.hobbyhive.viewmodel.SessionsViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SessionsScreen(viewModel: SessionsViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showLogDialog by remember { mutableStateOf(false) }
    var selectedHobbyId by remember { mutableLongStateOf(0L) }
    var durationText by remember { mutableStateOf("") }
    var notesText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GradientPageHeader(
                title = "Sessions", subtitle = "Track your practice time and build consistency.",
                badgeIcon = Icons.Default.Timer, badgeText = "Time Tracking",
                gradientColors = listOf(SessionGreen, SessionTeal, SessionCyan),
                actionButton = {
                    Button(onClick = { showLogDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PaperWhite, contentColor = InkBlack), border = BorderStroke(2.dp, InkBlack)) {
                        Icon(Icons.Default.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("Log Session", fontWeight = FontWeight.ExtraBold)
                    }
                }
            )
        }

        // Stats row
        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(12.dp)) {
                GradientStatCard("SESSIONS", "${state.totalSessions}", Icons.Default.EventNote, listOf(SessionGreen, SessionCyan), Modifier.weight(1f))
                GradientStatCard("TOTAL", viewModel.formatDuration(state.totalMinutes), Icons.Default.AccessTime, listOf(GoalIndigo, GoalViolet), Modifier.weight(1f))
            }
        }
        item {
            val avg = if (state.totalSessions > 0) state.totalMinutes / state.totalSessions else 0
            GradientStatCard("AVG DURATION", viewModel.formatDuration(avg), Icons.Default.TrendingUp, listOf(AnalyticsPurple, AnalyticsFuchsia), Modifier.fillMaxWidth(), subtitle = "per session")
        }

        // Search
        item {
            OutlinedTextField(
                value = state.searchQuery, onValueChange = { viewModel.setSearchQuery(it) },
                modifier = Modifier.fillMaxWidth(), placeholder = { Text("Search sessions...") },
                leadingIcon = { Icon(Icons.Default.Search, null) }, shape = RoundedCornerShape(16.dp), singleLine = true
            )
        }

        // Toast
        item { SuccessToast(state.successMessage ?: "", state.successMessage != null) }

        // Sessions timeline
        if (state.sessions.isEmpty() && !state.isLoading) {
            item { EmptyState(Icons.Default.Timer, "No sessions yet", "Log your first practice session to start tracking.", actionLabel = "Log Session", onAction = { showLogDialog = true }) }
        }

        state.sessions.forEachIndexed { idx, entry ->
            item {
                val df = SimpleDateFormat("MMM dd, yyyy • h:mm a", Locale.getDefault())
                TimelineItem(accentColor = HoneyYellow, isLast = idx == state.sessions.lastIndex) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        Column(Modifier.weight(1f)) {
                            Text(entry.hobbyName, fontWeight = FontWeight.ExtraBold, fontSize = 15.sp, color = InkBlack)
                            Text(df.format(Date(entry.session.sessionDate)), fontSize = 12.sp, color = Charcoal)
                            if (entry.session.notes.isNotBlank()) {
                                Text(entry.session.notes, fontSize = 12.sp, color = Charcoal, maxLines = 2)
                            }
                        }
                        Column(horizontalAlignment = Alignment.End) {
                            BadgeChip(viewModel.formatDuration(entry.session.durationMinutes), LimeGreen)
                            Spacer(Modifier.height(4.dp))
                            IconButton(onClick = { viewModel.deleteSession(entry.session) }, modifier = Modifier.size(28.dp)) {
                                Icon(Icons.Default.Delete, null, modifier = Modifier.size(16.dp), tint = ErrorRed.copy(alpha = 0.6f))
                            }
                        }
                    }
                }
            }
        }
    }

    // Log Session Dialog
    if (showLogDialog) {
        AlertDialog(
            onDismissRequest = { showLogDialog = false },
            title = { Text("Log Session", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    // Hobby selector
                    var expanded by remember { mutableStateOf(false) }
                    val selectedHobby = state.hobbies.find { it.id == selectedHobbyId }
                    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }) {
                        OutlinedTextField(
                            value = selectedHobby?.name ?: "Select hobby", onValueChange = {},
                            readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp),
                            trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) }
                        )
                        ExposedDropdownMenu(expanded, { expanded = false }) {
                            state.hobbies.forEach { hobby ->
                                DropdownMenuItem(text = { Text("${hobby.category.emoji} ${hobby.name}") }, onClick = { selectedHobbyId = hobby.id; expanded = false })
                            }
                        }
                    }
                    OutlinedTextField(
                        value = durationText, onValueChange = { durationText = it.filter { c -> c.isDigit() } },
                        label = { Text("Duration (minutes)") }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number)
                    )
                    OutlinedTextField(
                        value = notesText, onValueChange = { notesText = it },
                        label = { Text("Notes (optional)") }, modifier = Modifier.fillMaxWidth(),
                        shape = RoundedCornerShape(12.dp), maxLines = 3
                    )
                }
            },
            confirmButton = {
                Button(onClick = {
                    val mins = durationText.toIntOrNull() ?: 0
                    if (selectedHobbyId > 0 && mins in 1..1440) {
                        viewModel.logSession(selectedHobbyId, mins, System.currentTimeMillis(), notesText)
                        showLogDialog = false; durationText = ""; notesText = ""; selectedHobbyId = 0L
                    }
                }) { Text("Save", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showLogDialog = false }) { Text("Cancel") } }
        )
    }
}
