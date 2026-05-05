package com.example.hobbyhive.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.model.HobbyStatus
import com.example.hobbyhive.ui.components.DrawerContent
import com.example.hobbyhive.ui.components.HobbyCard
import com.example.hobbyhive.ui.theme.AccentAmber
import com.example.hobbyhive.ui.theme.AccentTeal
import com.example.hobbyhive.ui.theme.DotGridBackground
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun HomeScreen(
    hobbyRepository: HobbyRepository,
    onHobbyClick: (Long) -> Unit,
    onAddHobbyClick: () -> Unit,
    onNavigateToSettings: () -> Unit,
    onNavigateToProfile: () -> Unit,
    onNavigateToNotifications: () -> Unit,
    onLogout: () -> Unit
) {
    val tabs = listOf("All", "Active", "Completed", "Archived")
    var selectedTab by remember { mutableIntStateOf(0) }
    var isGridView by remember { mutableStateOf(false) }
    var searchQuery by remember { mutableStateOf("") }
    var showSearch by remember { mutableStateOf(false) }
    var showMenu by remember { mutableStateOf(false) }

    val hobbies by remember(selectedTab, searchQuery) {
        if (searchQuery.isNotBlank()) {
            hobbyRepository.searchHobbies(searchQuery)
        } else {
            when (selectedTab) {
                1 -> hobbyRepository.getHobbiesByStatus(HobbyStatus.ACTIVE)
                2 -> hobbyRepository.getHobbiesByStatus(HobbyStatus.COMPLETED)
                3 -> hobbyRepository.getHobbiesByStatus(HobbyStatus.ARCHIVED)
                else -> hobbyRepository.getAllHobbies()
            }
        }
    }.collectAsState(initial = emptyList())

    val drawerState = rememberDrawerState(DrawerValue.Closed)
    val scope = rememberCoroutineScope()
    val pagerState = rememberPagerState(pageCount = { tabs.size })

    LaunchedEffect(selectedTab) {
        pagerState.animateScrollToPage(selectedTab)
    }
    LaunchedEffect(pagerState.currentPage) {
        selectedTab = pagerState.currentPage
    }

    ModalNavigationDrawer(
        drawerState = drawerState,
        drawerContent = {
            DrawerContent(
                onItemClick = { route ->
                    scope.launch { drawerState.close() }
                    when (route) {
                        "profile" -> onNavigateToProfile()
                        "settings" -> onNavigateToSettings()
                        "notifications" -> onNavigateToNotifications()
                        "home" -> {}
                    }
                },
                onLogout = {
                    scope.launch { drawerState.close() }
                    onLogout()
                }
            )
        }
    ) {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = {
                        if (showSearch) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search hobbies...") },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                    unfocusedContainerColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                    focusedIndicatorColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                    unfocusedIndicatorColor = MaterialTheme.colorScheme.surface.copy(alpha = 0f),
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text("HobbyHive", fontWeight = FontWeight.Bold)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu")
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            showSearch = !showSearch
                            if (!showSearch) searchQuery = ""
                        }) {
                            Icon(if (showSearch) Icons.Default.Close else Icons.Default.Search, "Search")
                        }
                        IconButton(onClick = { isGridView = !isGridView }) {
                            Icon(if (isGridView) Icons.Default.ViewList else Icons.Default.GridView, "Toggle view")
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, "More")
                            }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(text = { Text("Sort by Name") }, onClick = { showMenu = false })
                                DropdownMenuItem(text = { Text("Sort by Rating") }, onClick = { showMenu = false })
                                DropdownMenuItem(text = { Text("Sort by Progress") }, onClick = { showMenu = false })
                            }
                        }
                    }
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onAddHobbyClick,
                    icon = { Icon(Icons.Default.Add, "Add") },
                    text = { Text("Add Hobby") },
                    containerColor = MaterialTheme.colorScheme.primary,
                    contentColor = MaterialTheme.colorScheme.onPrimary
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                DotGridBackground()

                Column(modifier = Modifier.fillMaxSize()) {
                    // TabRow
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        edgePadding = 16.dp,
                        containerColor = MaterialTheme.colorScheme.surface,
                        contentColor = MaterialTheme.colorScheme.primary,
                        divider = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            Tab(
                                selected = selectedTab == index,
                                onClick = { selectedTab = index },
                                text = {
                                    Text(
                                        title,
                                        fontWeight = if (selectedTab == index) FontWeight.Bold else FontWeight.Normal
                                    )
                                }
                            )
                        }
                    }

                    // Content
                    HorizontalPager(state = pagerState, modifier = Modifier.fillMaxSize()) { page ->
                        if (hobbies.isEmpty()) {
                            Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                                    Text("🐝", style = MaterialTheme.typography.displayLarge)
                                    Spacer(Modifier.height(16.dp))
                                    Text("No hobbies yet!", style = MaterialTheme.typography.titleMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Tap + to discover your first hobby", style = MaterialTheme.typography.bodyMedium, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                }
                            }
                        } else if (isGridView) {
                            LazyVerticalGrid(
                                columns = GridCells.Fixed(2),
                                contentPadding = PaddingValues(16.dp),
                                horizontalArrangement = Arrangement.spacedBy(12.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(hobbies, key = { it.id }) { hobby ->
                                    HobbyCard(hobby = hobby, onClick = { onHobbyClick(hobby.id) })
                                }
                            }
                        } else {
                            LazyColumn(
                                contentPadding = PaddingValues(16.dp),
                                verticalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                items(hobbies, key = { it.id }) { hobby ->
                                    HobbyCard(hobby = hobby, onClick = { onHobbyClick(hobby.id) })
                                }
                            }
                        }
                    }
                }
            }
        }
    }
}
