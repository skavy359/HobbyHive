package com.example.hobbyhive.ui.screens

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.pager.HorizontalPager
import androidx.compose.foundation.pager.rememberPagerState
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.data.HobbyRepository
import com.example.hobbyhive.model.Hobby
import com.example.hobbyhive.model.HobbyStatus
import com.example.hobbyhive.ui.components.DrawerContent
import com.example.hobbyhive.ui.components.HobbyCard
import com.example.hobbyhive.ui.theme.*
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
            containerColor = PaperCream,
            topBar = {
                TopAppBar(
                    title = {
                        if (showSearch) {
                            TextField(
                                value = searchQuery,
                                onValueChange = { searchQuery = it },
                                placeholder = { Text("Search hobbies...", color = Charcoal) },
                                singleLine = true,
                                colors = TextFieldDefaults.colors(
                                    focusedContainerColor = Color.Transparent,
                                    unfocusedContainerColor = Color.Transparent,
                                    focusedIndicatorColor = Color.Transparent,
                                    unfocusedIndicatorColor = Color.Transparent,
                                    focusedTextColor = InkBlack,
                                    cursorColor = HoneyYellow,
                                ),
                                modifier = Modifier.fillMaxWidth()
                            )
                        } else {
                            Text("🐝 HobbyHive", fontWeight = FontWeight.Black, color = InkBlack, fontSize = 20.sp)
                        }
                    },
                    navigationIcon = {
                        IconButton(onClick = { scope.launch { drawerState.open() } }) {
                            Icon(Icons.Default.Menu, "Menu", tint = InkBlack)
                        }
                    },
                    actions = {
                        IconButton(onClick = {
                            showSearch = !showSearch
                            if (!showSearch) searchQuery = ""
                        }) {
                            Icon(if (showSearch) Icons.Default.Close else Icons.Default.Search, "Search", tint = InkBlack)
                        }
                        IconButton(onClick = { isGridView = !isGridView }) {
                            Icon(if (isGridView) Icons.Default.ViewList else Icons.Default.GridView, "Toggle view", tint = InkBlack)
                        }
                        Box {
                            IconButton(onClick = { showMenu = true }) {
                                Icon(Icons.Default.MoreVert, "More", tint = InkBlack)
                            }
                            DropdownMenu(expanded = showMenu, onDismissRequest = { showMenu = false }) {
                                DropdownMenuItem(text = { Text("Sort by Name", fontWeight = FontWeight.Bold) }, onClick = { showMenu = false })
                                DropdownMenuItem(text = { Text("Sort by Rating", fontWeight = FontWeight.Bold) }, onClick = { showMenu = false })
                                DropdownMenuItem(text = { Text("Sort by Progress", fontWeight = FontWeight.Bold) }, onClick = { showMenu = false })
                            }
                        }
                    },
                    colors = TopAppBarDefaults.topAppBarColors(
                        containerColor = PaperCream,
                        titleContentColor = InkBlack
                    )
                )
            },
            floatingActionButton = {
                ExtendedFloatingActionButton(
                    onClick = onAddHobbyClick,
                    icon = { Icon(Icons.Default.Add, "Add", tint = InkBlack) },
                    text = { Text("Add Hobby", fontWeight = FontWeight.ExtraBold, color = InkBlack) },
                    containerColor = HoneyYellow,
                    contentColor = InkBlack,
                    shape = RoundedCornerShape(16.dp),
                    modifier = Modifier.border(2.5.dp, InkBlack, RoundedCornerShape(16.dp))
                )
            }
        ) { padding ->
            Box(modifier = Modifier.fillMaxSize().padding(padding)) {
                DotGridBackground()

                Column(modifier = Modifier.fillMaxSize()) {
                    // TabRow — sticker style
                    ScrollableTabRow(
                        selectedTabIndex = selectedTab,
                        edgePadding = 16.dp,
                        containerColor = PaperCream,
                        contentColor = InkBlack,
                        divider = {},
                        indicator = {}
                    ) {
                        tabs.forEachIndexed { index, title ->
                            val isSelected = selectedTab == index
                            Tab(
                                selected = isSelected,
                                onClick = { selectedTab = index },
                                modifier = Modifier
                                    .padding(horizontal = 4.dp, vertical = 4.dp)
                                    .clip(RoundedCornerShape(50))
                                    .then(
                                        if (isSelected) Modifier.border(2.dp, InkBlack, RoundedCornerShape(50))
                                        else Modifier.border(1.dp, InkBlack.copy(alpha = 0.2f), RoundedCornerShape(50))
                                    ),
                                text = {
                                    Text(
                                        title,
                                        fontWeight = if (isSelected) FontWeight.ExtraBold else FontWeight.SemiBold,
                                        color = InkBlack,
                                        fontSize = 13.sp,
                                        modifier = Modifier.padding(horizontal = 4.dp)
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
                                    BeeMascot(size = 100.dp)
                                    Spacer(Modifier.height(16.dp))
                                    Text("No hobbies yet!", fontWeight = FontWeight.Black, fontSize = 18.sp, color = InkBlack)
                                    Spacer(Modifier.height(8.dp))
                                    Text("Tap + to discover your first hobby", fontSize = 14.sp, color = Charcoal, fontWeight = FontWeight.SemiBold)
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
