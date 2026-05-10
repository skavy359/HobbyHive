package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hobbyhive.model.EventStatus
import com.example.hobbyhive.ui.components.*
import com.example.hobbyhive.ui.theme.*
import com.example.hobbyhive.viewmodel.StudyPlannerViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun StudyPlannerScreen(viewModel: StudyPlannerViewModel = viewModel()) {
    val state by viewModel.uiState.collectAsState()
    var showCreateDialog by remember { mutableStateOf(false) }
    var titleText by remember { mutableStateOf("") }
    var descText by remember { mutableStateOf("") }

    LazyColumn(
        modifier = Modifier.fillMaxSize().padding(horizontal = 16.dp),
        contentPadding = PaddingValues(vertical = 16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        item {
            GradientPageHeader(
                title = "Study Planner", subtitle = "Plan sessions and stay on track with your calendar.",
                badgeIcon = Icons.Default.CalendarMonth, badgeText = "Calendar View",
                gradientColors = listOf(PlannerIndigo, PlannerPurple, PlannerFuchsia),
                actionButton = {
                    Button(onClick = { showCreateDialog = true }, colors = ButtonDefaults.buttonColors(containerColor = PaperWhite, contentColor = InkBlack), border = BorderStroke(2.dp, InkBlack)) {
                        Icon(Icons.Default.Add, null, Modifier.size(18.dp)); Spacer(Modifier.width(6.dp)); Text("New Event", fontWeight = FontWeight.Bold)
                    }
                }
            )
        }

        item {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.spacedBy(10.dp)) {
                GradientStatCard("PLANNED", "${state.plannedCount}", Icons.Default.Schedule, listOf(PlannerIndigo, PlannerPurple), Modifier.weight(1f))
                GradientStatCard("DONE", "${state.completedCount}", Icons.Default.CheckCircle, listOf(SessionGreen, SessionCyan), Modifier.weight(1f))
                GradientStatCard("MISSED", "${state.missedCount}", Icons.Default.Close, listOf(StreakOrange, StreakRose), Modifier.weight(1f))
            }
        }

        item { SuccessToast(state.successMessage ?: "", state.successMessage != null) }

        // Calendar
        item {
            val cal = Calendar.getInstance().apply { set(Calendar.YEAR, state.selectedYear); set(Calendar.MONTH, state.selectedMonth); set(Calendar.DAY_OF_MONTH, 1) }
            val monthName = SimpleDateFormat("MMMM yyyy", Locale.getDefault()).format(cal.time)
            val daysInMonth = cal.getActualMaximum(Calendar.DAY_OF_MONTH)
            val firstDayOfWeek = cal.get(Calendar.DAY_OF_WEEK) - 1
            val datesWithEvents = viewModel.getDatesWithEvents()

            Card(shape = RoundedCornerShape(24.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack)) {
                Column(Modifier.padding(20.dp)) {
                    Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                        IconButton(onClick = {
                            val prev = if (state.selectedMonth == 0) 11 else state.selectedMonth - 1
                            val yr = if (state.selectedMonth == 0) state.selectedYear - 1 else state.selectedYear
                            viewModel.selectMonth(prev, yr)
                        }) { Icon(Icons.Default.ChevronLeft, null) }
                        Text(monthName, fontWeight = FontWeight.Bold, fontSize = 18.sp)
                        IconButton(onClick = {
                            val next = if (state.selectedMonth == 11) 0 else state.selectedMonth + 1
                            val yr = if (state.selectedMonth == 11) state.selectedYear + 1 else state.selectedYear
                            viewModel.selectMonth(next, yr)
                        }) { Icon(Icons.Default.ChevronRight, null) }
                    }

                    Spacer(Modifier.height(12.dp))
                    Row(Modifier.fillMaxWidth()) {
                        listOf("S","M","T","W","T","F","S").forEach { d ->
                            Text(d, Modifier.weight(1f), textAlign = TextAlign.Center, fontWeight = FontWeight.Bold, fontSize = 12.sp, color = Charcoal)
                        }
                    }
                    Spacer(Modifier.height(8.dp))

                    val today = Calendar.getInstance()
                    val totalCells = firstDayOfWeek + daysInMonth
                    val rows = (totalCells + 6) / 7

                    for (r in 0 until rows) {
                        Row(Modifier.fillMaxWidth()) {
                            for (c in 0 until 7) {
                                val idx = r * 7 + c
                                val day = idx - firstDayOfWeek + 1
                                Box(Modifier.weight(1f).aspectRatio(1f).padding(2.dp), contentAlignment = Alignment.Center) {
                                    if (day in 1..daysInMonth) {
                                        val dayCal = Calendar.getInstance().apply {
                                            set(Calendar.YEAR, state.selectedYear); set(Calendar.MONTH, state.selectedMonth); set(Calendar.DAY_OF_MONTH, day)
                                            set(Calendar.HOUR_OF_DAY, 0); set(Calendar.MINUTE, 0); set(Calendar.SECOND, 0); set(Calendar.MILLISECOND, 0)
                                        }
                                        val isToday = today.get(Calendar.YEAR) == state.selectedYear && today.get(Calendar.MONTH) == state.selectedMonth && today.get(Calendar.DAY_OF_MONTH) == day
                                        val isSelected = dayCal.timeInMillis == state.selectedDate
                                        val hasEvent = datesWithEvents.contains(dayCal.timeInMillis)
                                        val bg = when { isSelected -> PlannerIndigo; isToday -> PlannerIndigo.copy(alpha = 0.15f); else -> Color.Transparent }
                                        val txtColor = when { isSelected -> Color.White; isToday -> PlannerIndigo; else -> InkBlack }
                                        Column(horizontalAlignment = Alignment.CenterHorizontally, modifier = Modifier.clip(RoundedCornerShape(10.dp)).background(bg).clickable { viewModel.selectDate(dayCal.timeInMillis) }.padding(4.dp)) {
                                            Text("$day", color = txtColor, fontSize = 13.sp, fontWeight = if (isToday || isSelected) FontWeight.Bold else FontWeight.Normal)
                                            if (hasEvent) Box(Modifier.size(4.dp).clip(CircleShape).background(if (isSelected) Color.White else PlannerPurple))
                                        }
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }

        // Events for selected date
        item {
            val df = SimpleDateFormat("EEEE, MMMM d", Locale.getDefault())
            SectionHeader(df.format(Date(state.selectedDate)))
        }

        val dayEvents = viewModel.getEventsForDate(state.selectedDate)
        if (dayEvents.isEmpty()) {
            item { EmptyState(Icons.Default.EventBusy, "No events", "Schedule something for this day!", actionLabel = "Add Event", onAction = { showCreateDialog = true }) }
        }

        dayEvents.forEach { event ->
            item {
                val timeF = SimpleDateFormat("h:mm a", Locale.getDefault())
                val statusColor = when (event.status) { EventStatus.PLANNED -> StatusPlanned; EventStatus.COMPLETED -> StatusCompleted; EventStatus.MISSED -> StatusMissed; EventStatus.CANCELLED -> StatusCancelled }
                Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack), modifier = Modifier.fillMaxWidth()) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                            Text(event.title, fontWeight = FontWeight.Bold, fontSize = 15.sp, modifier = Modifier.weight(1f))
                            BadgeChip(event.status.displayName, statusColor)
                        }
                        Spacer(Modifier.height(4.dp))
                        Text("${timeF.format(Date(event.startTime))} — ${timeF.format(Date(event.endTime))}", fontSize = 12.sp, color = Charcoal)
                        if (event.description.isNotBlank()) { Spacer(Modifier.height(4.dp)); Text(event.description, fontSize = 12.sp, color = Charcoal) }
                        if (event.status == EventStatus.PLANNED) {
                            Spacer(Modifier.height(12.dp))
                            Row(horizontalArrangement = Arrangement.spacedBy(8.dp)) {
                                Button(onClick = { viewModel.markComplete(event) }, modifier = Modifier.weight(1f), colors = ButtonDefaults.buttonColors(containerColor = SuccessGreen)) { Text("Done ✅", fontWeight = FontWeight.Bold) }
                                OutlinedButton(onClick = { viewModel.deleteEvent(event) }) { Icon(Icons.Default.Delete, null, Modifier.size(16.dp), tint = ErrorRed) }
                            }
                        }
                    }
                }
            }
        }
    }

    if (showCreateDialog) {
        AlertDialog(
            onDismissRequest = { showCreateDialog = false },
            title = { Text("New Event", fontWeight = FontWeight.Bold) },
            text = {
                Column(verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    OutlinedTextField(value = titleText, onValueChange = { titleText = it }, label = { Text("Event title") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                    OutlinedTextField(value = descText, onValueChange = { descText = it }, label = { Text("Description") }, modifier = Modifier.fillMaxWidth(), shape = RoundedCornerShape(12.dp))
                }
            },
            confirmButton = {
                Button(onClick = {
                    if (titleText.isNotBlank()) {
                        val start = state.selectedDate + 9 * 3600000L
                        viewModel.createEvent(titleText, descText, null, start, start + 3600000L, "#6366f1")
                        showCreateDialog = false; titleText = ""; descText = ""
                    }
                }) { Text("Create", fontWeight = FontWeight.Bold) }
            },
            dismissButton = { TextButton(onClick = { showCreateDialog = false }) { Text("Cancel") } }
        )
    }
}
