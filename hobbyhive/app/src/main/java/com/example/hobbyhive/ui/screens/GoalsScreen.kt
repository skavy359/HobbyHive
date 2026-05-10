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
import com.example.hobbyhive.model.GoalStatus
import com.example.hobbyhive.ui.components.*
import com.example.hobbyhive.ui.theme.*
import com.example.hobbyhive.viewmodel.GoalsViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GoalsScreen(viewModel: GoalsViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var titleText by remember { mutableStateOf("") }
    var descText by remember { mutableStateOf("") }
    var targetText by remember { mutableStateOf("") }
    var selectedHobbyId by remember { mutableLongStateOf(0L) }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GradientPageHeader(
                title = "Goals", subtitle = "Set targets and track your progress toward mastery.",
                badgeIcon = Icons.Default.Flag, badgeText = "Goal Tracking",
                gradientColors = listOf(GoalIndigo, GoalViolet),
                actionButton = {
                    Button(onClick = { showCreateDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PaperWhite, contentColor = InkBlack), border = BorderStroke(2.dp, InkBlack)) {
                        Icon(Icons.Default.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("New Goal", fontWeight = FontWeight.ExtraBold)
                    }
                }
            )
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GradientStatCard("ACTIVE", "${state.activeCount}", Icons.Default.TrendingUp, listOf(GoalIndigo, GoalViolet), Modifier.weight(1f))
                GradientStatCard("DONE", "${state.completedCount}", Icons.Default.CheckCircle, listOf(SessionGreen, SessionCyan), Modifier.weight(1f))
                GradientStatCard("AT RISK", "${state.atRiskCount}", Icons.Default.Warning, listOf(StreakOrange, StreakRose), Modifier.weight(1f))
            }
        }

        item { SuccessToast(state.successMessage ?: "", state.successMessage != null) }

        if (state.goals.isEmpty() && !state.isLoading) {
            item { EmptyState(Icons.Default.Flag, "No goals yet", "Create your first goal to start tracking progress!", actionLabel = "Create Goal", onAction = { showCreateDialog = true }) }
        }

        state.goals.forEach { entry ->
            item {
                val statusColor = when (entry.goal.status) {
                    GoalStatus.IN_PROGRESS -> GoalIndigo; GoalStatus.COMPLETED -> SuccessGreen
                    GoalStatus.AT_RISK -> AccentAmber; GoalStatus.FAILED -> ErrorRed
                }
                Card(shape = RoundedCornerShape(20.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(20.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Column(Modifier.weight(1f)) {
                                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                    Text(entry.hobbyEmoji, fontSize = 20.sp)
                                    Text(entry.goal.title, fontWeight = FontWeight.ExtraBold, fontSize = 16.sp, color = InkBlack)
                                }
                                Text(entry.hobbyName, fontSize = 12.sp, color = Charcoal)
                            }
                            BadgeChip(entry.goal.status.displayName, statusColor)
                        }
                        Spacer(Modifier.height(16.dp))
                        Row(Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
                            RingChart(progress = entry.goal.progressPercent, size = 64.dp, strokeWidth = 8.dp, color = statusColor, modifier = Modifier.padding(end = 16.dp))
                            Column(Modifier.weight(1f)) {
                                GradientProgressBar(progress = entry.goal.progressPercent / 100f, colors = listOf(GoalIndigo, GoalViolet))
                                Spacer(Modifier.height(6.dp))
                                Text("${entry.goal.currentValue} / ${entry.goal.targetValue} ${entry.goal.unit}", fontSize = 12.sp, fontWeight = FontWeight.Bold, color = Charcoal)
                            }
                        }
                        if (entry.goal.description.isNotBlank()) {
                            Spacer(Modifier.height(8.dp))
                            Text(entry.goal.description, fontSize = 13.sp, color = Charcoal)
                        }
                        Spacer(Modifier.height(12.dp))
                        Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                            if (entry.goal.status != GoalStatus.COMPLETED) {
                                OutlinedButton(onClick = { viewModel.updateGoalProgress(entry.goal, entry.goal.currentValue + 1) }, modifier = Modifier.weight(1f)) { Text("+1", fontWeight = FontWeight.Bold) }
                            }
                            OutlinedButton(onClick = { viewModel.deleteGoal(entry.goal) }, colors = ButtonDefaults.outlinedButtonColors(contentColor = ErrorRed)) { Icon(Icons.Default.Delete, null, Modifier.size(16.dp)) }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("Create Goal", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    var expanded by remember { mutableStateOf(false) }
                    val sel = state.hobbies.find { it.id == selectedHobbyId }
                    ExposedDropdownMenuBox(expanded, { expanded = !expanded }) {
                        OutlinedTextField(value = sel?.name ?: "Select hobby", onValueChange = {}, readOnly = true, modifier = Modifier.fillMaxWidth().menuAnchor(), shape = RoundedCornerShape(12.dp), trailingIcon = { ExposedDropdownMenuDefaults.TrailingIcon(expanded) })
                        ExposedDropdownMenu(expanded, { expanded = false }) {
                            state.hobbies.forEach { h -> DropdownMenuItem(text = { Text("${h.category.emoji} ${h.name}") }, onClick = { selectedHobbyId = h.id; expanded = false }) }
                        }
                    }
                    OutlinedTextField(value = titleText, onValueChange = { titleText = it }, label = { Text("Goal title") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = descText, onValueChange = { descText = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = targetText, onValueChange = { targetText = it.filter { c -> c.isDigit() } }, label = { Text("Target value") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp), keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.Number))
                }
            },
            confirmButton = {
                Button(onClick = {
                    val target = targetText.toIntOrNull() ?: 0
                    if (selectedHobbyId > 0 && titleText.isNotBlank() && target > 0) {
                        viewModel.createGoal(selectedHobbyId, titleText, descText, target, "hours", null)
                        showCreateDialog = false; titleText = ""; descText = ""; targetText = ""; selectedHobbyId = 0L
                    }
                }) { Text("Create", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") } }
        )
    }
}
