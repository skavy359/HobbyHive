package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.model.HobbyCategory
import com.example.hobbyhive.ui.theme.*
import com.example.hobbyhive.model.HobbyStatus
import com.example.hobbyhive.ui.components.HobbyButton
import com.example.hobbyhive.ui.components.HobbyTextField
import com.example.hobbyhive.ui.components.RatingBar
import com.example.hobbyhive.ui.theme.DotGridBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AddEditHobbyScreen(
    hobbyId: Long?,
    hobbyRepository: HobbyRepository,
    onNavigateBack: () -> Unit
) {
    val isEdit = hobbyId != null && hobbyId > 0
    val scope = rememberCoroutineScope()

    var name by remember { mutableStateOf("") }
    var description by remember { mutableStateOf("") }
    var notes by remember { mutableStateOf("") }
    var category by remember { mutableStateOf(HobbyCategory.OTHER) }
    var status by remember { mutableStateOf(HobbyStatus.ACTIVE) }
    var rating by remember { mutableFloatStateOf(0f) }
    var progress by remember { mutableIntStateOf(0) }
    var nameError by remember { mutableStateOf<String?>(null) }
    var isLoading by remember { mutableStateOf(false) }
    var categoryExpanded by remember { mutableStateOf(false) }
    var statusExpanded by remember { mutableStateOf(false) }
    var existingHobby by remember { mutableStateOf<Hobby?>(null) }

    // Load existing hobby for edit mode
    LaunchedEffect(hobbyId) {
        if (isEdit && hobbyId != null) {
            hobbyRepository.getHobbyByIdOnce(hobbyId)?.let { h ->
                existingHobby = h
                name = h.name
                description = h.description
                notes = h.notes
                category = h.category
                status = h.status
                rating = h.rating
                progress = h.progress
            }
        }
    }

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(if (isEdit) "Edit Hobby" else "Add Hobby", fontWeight = FontWeight.Bold) },
                navigationIcon = {
                    IconButton(onClick = onNavigateBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back")
                    }
                }
            )
        }
    ) { padding ->
        Box(modifier = Modifier.fillMaxSize().padding(padding)) {
            DotGridBackground()

            Column(
                modifier = Modifier.fillMaxSize().verticalScroll(rememberScrollState()).padding(16.dp)
            ) {
                // Name
                HobbyTextField(
                    value = name,
                    onValueChange = { name = it; nameError = null },
                    label = "Hobby Name",
                    leadingIcon = Icons.Default.Interests,
                    errorMessage = nameError
                )

                Spacer(Modifier.height(16.dp))

                // Category dropdown
                ExposedDropdownMenuBox(expanded = categoryExpanded, onExpandedChange = { categoryExpanded = it }) {
                    HobbyTextField(
                        value = "${category.emoji} ${category.displayName}",
                        onValueChange = {},
                        label = "Category",
                        leadingIcon = Icons.Default.Category,
                        readOnly = true,
                        modifier = Modifier.menuAnchor()
                    )
                    ExposedDropdownMenu(expanded = categoryExpanded, onDismissRequest = { categoryExpanded = false }) {
                        HobbyCategory.entries.forEach { cat ->
                            DropdownMenuItem(
                                text = { Text("${cat.emoji} ${cat.displayName}") },
                                onClick = { category = cat; categoryExpanded = false }
                            )
                        }
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Status dropdown (only in edit mode)
                if (isEdit) {
                    ExposedDropdownMenuBox(expanded = statusExpanded, onExpandedChange = { statusExpanded = it }) {
                        HobbyTextField(
                            value = status.displayName,
                            onValueChange = {},
                            label = "Status",
                            leadingIcon = Icons.Default.Flag,
                            readOnly = true,
                            modifier = Modifier.menuAnchor()
                        )
                        ExposedDropdownMenu(expanded = statusExpanded, onDismissRequest = { statusExpanded = false }) {
                            HobbyStatus.entries.forEach { s ->
                                DropdownMenuItem(
                                    text = { Text(s.displayName) },
                                    onClick = { status = s; statusExpanded = false }
                                )
                            }
                        }
                    }
                    Spacer(Modifier.height(16.dp))
                }

                // Description
                HobbyTextField(
                    value = description,
                    onValueChange = { description = it },
                    label = "Description",
                    leadingIcon = Icons.Default.Description,
                    singleLine = false,
                    maxLines = 4
                )

                Spacer(Modifier.height(16.dp))

                // Notes
                HobbyTextField(
                    value = notes,
                    onValueChange = { notes = it },
                    label = "Notes",
                    leadingIcon = Icons.Default.Notes,
                    singleLine = false,
                    maxLines = 4
                )

                Spacer(Modifier.height(24.dp))

                // Rating
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = PaperWhite)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Text("Rating", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                        Spacer(Modifier.height(8.dp))
                        RatingBar(rating = rating, onRatingChanged = { rating = it }, starSize = 40.dp)
                    }
                }

                Spacer(Modifier.height(16.dp))

                // Progress slider
                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = MaterialTheme.shapes.medium,
                    colors = CardDefaults.cardColors(containerColor = PaperWhite)
                ) {
                    Column(Modifier.padding(16.dp)) {
                        Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween) {
                            Text("Progress", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.SemiBold)
                            Text("${progress}%", style = MaterialTheme.typography.titleSmall, fontWeight = FontWeight.Bold, color = MaterialTheme.colorScheme.primary)
                        }
                        Spacer(Modifier.height(8.dp))
                        Slider(
                            value = progress.toFloat(),
                            onValueChange = { progress = it.toInt() },
                            valueRange = 0f..100f,
                            steps = 19,
                            colors = SliderDefaults.colors(
                                thumbColor = MaterialTheme.colorScheme.primary,
                                activeTrackColor = MaterialTheme.colorScheme.primary
                            )
                        )
                    }
                }

                Spacer(Modifier.height(32.dp))

                // Save button
                HobbyButton(
                    text = if (isEdit) "Update Hobby" else "Create Hobby",
                    onClick = {
                        if (name.isBlank()) { nameError = "Please enter a hobby name"; return@HobbyButton }
                        isLoading = true
                        scope.launch {
                            if (isEdit && existingHobby != null) {
                                hobbyRepository.updateHobby(existingHobby!!.copy(
                                    name = name.trim(), description = description.trim(), notes = notes.trim(),
                                    category = category, status = status, rating = rating, progress = progress
                                ))
                            } else {
                                hobbyRepository.insertHobby(Hobby(
                                    name = name.trim(), description = description.trim(), notes = notes.trim(),
                                    category = category, status = status, rating = rating, progress = progress
                                ))
                            }
                            isLoading = false
                            onNavigateBack()
                        }
                    },
                    isLoading = isLoading
                )

                Spacer(Modifier.height(32.dp))
            }
        }
    }
}
