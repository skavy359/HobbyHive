package com.example.hobbyhive.ui.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.foundation.background
import androidx.lifecycle.viewmodel.compose.viewModel
import com.example.hobbyhive.model.ForumComment
import com.example.hobbyhive.model.ForumPost
import com.example.hobbyhive.model.HobbyGroup
import com.example.hobbyhive.ui.components.BadgeChip
import com.example.hobbyhive.ui.components.GradientPageHeader
import com.example.hobbyhive.ui.theme.*
import com.example.hobbyhive.viewmodel.CommunityViewModel
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CommunityScreen(
    onNavigateBack: () -> Unit,
    onNavigateToPost: (Long) -> Unit,
    viewModel: CommunityViewModel = viewModel()
) {
    var activeTab by remember { mutableStateOf(0) } // 0: Forum, 1: Groups
    var showCreatePostDialog by remember { mutableStateOf(false) }

    val posts by viewModel.posts.collectAsState()
    val groups by viewModel.groups.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { },
                navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }
            )
        },
        floatingActionButton = {
            if (activeTab == 0) {
                FloatingActionButton(onClick = { showCreatePostDialog = true }, containerColor = ThemeOrange) {
                    Icon(Icons.Default.Add, "New Post", tint = Color.White)
                }
            }
        }
    ) { padding ->
        Column(Modifier.fillMaxSize().padding(padding)) {
            Box(Modifier.padding(horizontal = 16.dp)) {
                GradientPageHeader(
                    title = "Community", subtitle = "Connect with other enthusiasts.",
                    badgeIcon = Icons.Default.Groups, badgeText = "Hobby Hub",
                    gradientColors = listOf(ThemeBlue, ThemeIndigo)
                )
            }
            
            Spacer(Modifier.height(16.dp))
            TabRow(
                selectedTabIndex = activeTab,
                containerColor = Color.Transparent,
                contentColor = ThemeIndigo
            ) {
                Tab(selected = activeTab == 0, onClick = { activeTab = 0 }, text = { Text("Forums", fontWeight = FontWeight.Bold) })
                Tab(selected = activeTab == 1, onClick = { activeTab = 1 }, text = { Text("Groups", fontWeight = FontWeight.Bold) })
            }
            
            if (activeTab == 0) {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    if (posts.isEmpty()) item { Text("No posts yet. Be the first to ask a question!", Modifier.padding(16.dp)) }
                    items(posts) { post -> ForumPostCard(post, { viewModel.upvotePost(post) }, { onNavigateToPost(post.id) }) }
                }
            } else {
                LazyColumn(contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(12.dp)) {
                    items(groups) { group -> GroupCard(group) { viewModel.toggleGroupJoin(group) } }
                }
            }
        }
        
        if (showCreatePostDialog) {
            CreatePostDialog(
                onDismiss = { showCreatePostDialog = false },
                onSubmit = { title, content, cat -> viewModel.createPost(title, content, cat); showCreatePostDialog = false }
            )
        }
    }
}

@Composable
fun ForumPostCard(post: ForumPost, onUpvote: () -> Unit, onClick: () -> Unit) {
    val df = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack), modifier = Modifier.fillMaxWidth().clickable(onClick = onClick)) {
        Column(Modifier.padding(16.dp)) {
            Row(Modifier.fillMaxWidth(), horizontalArrangement = Arrangement.SpaceBetween, verticalAlignment = Alignment.CenterVertically) {
                BadgeChip(post.category, ThemeBlue)
                Text(df.format(Date(post.createdAt)), fontSize = 11.sp, color = Charcoal)
            }
            Spacer(Modifier.height(8.dp))
            Text(post.title, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            Text("by ${post.authorName}", fontSize = 12.sp, color = Charcoal)
            Spacer(Modifier.height(12.dp))
            Row(horizontalArrangement = Arrangement.spacedBy(16.dp)) {
                Row(verticalAlignment = Alignment.CenterVertically, modifier = Modifier.clickable { onUpvote() }) {
                    Icon(Icons.Default.ThumbUp, null, Modifier.size(16.dp), tint = ThemeOrange)
                    Spacer(Modifier.width(4.dp))
                    Text("${post.upvotes}", fontSize = 13.sp, fontWeight = FontWeight.Bold)
                }
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.ChatBubbleOutline, null, Modifier.size(16.dp), tint = Charcoal)
                    Spacer(Modifier.width(4.dp))
                    Text("${post.repliesCount}", fontSize = 13.sp, color = Charcoal)
                }
            }
        }
    }
}

@Composable
fun GroupCard(group: HobbyGroup, onJoinToggle: () -> Unit) {
    Card(shape = RoundedCornerShape(16.dp), colors = CardDefaults.cardColors(containerColor = PaperWhite), border = BorderStroke(2.dp, InkBlack), modifier = Modifier.fillMaxWidth()) {
        Row(Modifier.padding(16.dp), verticalAlignment = Alignment.CenterVertically) {
            Surface(Modifier.size(50.dp), shape = RoundedCornerShape(12.dp), color = ThemeIndigo.copy(alpha = 0.1f)) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.Groups, null, tint = ThemeIndigo, modifier = Modifier.size(24.dp)) }
            }
            Spacer(Modifier.width(16.dp))
            Column(Modifier.weight(1f)) {
                Text(group.name, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(group.description, fontSize = 13.sp, color = Charcoal, maxLines = 2)
                Spacer(Modifier.height(4.dp))
                Text("${group.membersCount} members • ${group.category}", fontSize = 12.sp, color = ThemeBlue, fontWeight = FontWeight.SemiBold)
            }
            Button(
                onClick = onJoinToggle,
                colors = ButtonDefaults.buttonColors(containerColor = if (group.isJoined) PaperWarm else ThemeIndigo, contentColor = if (group.isJoined) InkBlack else Color.White)
            ) {
                Text(if (group.isJoined) "Joined" else "Join")
            }
        }
    }
}

@Composable
fun CreatePostDialog(onDismiss: () -> Unit, onSubmit: (String, String, String) -> Unit) {
    var title by remember { mutableStateOf("") }
    var content by remember { mutableStateOf("") }
    var category by remember { mutableStateOf("General") }

    AlertDialog(
        onDismissRequest = onDismiss,
        title = { Text("Create Post", fontWeight = FontWeight.Bold) },
        text = {
            Column(verticalArrangement = Arrangement.spacedBy(8.dp)) {
                OutlinedTextField(value = title, onValueChange = { title = it }, label = { Text("Title") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = category, onValueChange = { category = it }, label = { Text("Category (e.g. Art, Tech)") }, singleLine = true, modifier = Modifier.fillMaxWidth())
                OutlinedTextField(value = content, onValueChange = { content = it }, label = { Text("Content") }, minLines = 3, modifier = Modifier.fillMaxWidth())
            }
        },
        confirmButton = { Button(onClick = { if (title.isNotBlank() && content.isNotBlank()) onSubmit(title, content, category) }) { Text("Post") } },
        dismissButton = { TextButton(onClick = onDismiss) { Text("Cancel") } }
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun PostDetailScreen(postId: Long, onNavigateBack: () -> Unit, viewModel: CommunityViewModel = viewModel()) {
    val post by viewModel.getPost(postId).collectAsState(initial = null)
    val comments by viewModel.getComments(postId).collectAsState(initial = emptyList())
    var newComment by remember { mutableStateOf("") }
    val df = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())

    Scaffold(
        topBar = { TopAppBar(title = { Text("Discussion") }, navigationIcon = { IconButton(onClick = onNavigateBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, "Back") } }) },
        bottomBar = {
            Surface(shadowElevation = 8.dp, color = MaterialTheme.colorScheme.surface) {
                Row(Modifier.padding(16.dp).navigationBarsPadding(), verticalAlignment = Alignment.CenterVertically) {
                    OutlinedTextField(value = newComment, onValueChange = { newComment = it }, placeholder = { Text("Add a comment...") }, modifier = Modifier.weight(1f), shape = RoundedCornerShape(24.dp))
                    Spacer(Modifier.width(8.dp))
                    IconButton(onClick = { if (newComment.isNotBlank()) { viewModel.addComment(postId, newComment); newComment = "" } }, modifier = Modifier.background(ThemeIndigo, RoundedCornerShape(50)).size(48.dp)) {
                        Icon(Icons.Default.Send, "Send", tint = Color.White)
                    }
                }
            }
        }
    ) { padding ->
        LazyColumn(Modifier.fillMaxSize().padding(padding), contentPadding = PaddingValues(16.dp), verticalArrangement = Arrangement.spacedBy(16.dp)) {
            post?.let { p ->
                item {
                    Column {
                        BadgeChip(p.category, ThemeBlue)
                        Spacer(Modifier.height(8.dp))
                        Text(p.title, fontSize = 22.sp, fontWeight = FontWeight.Black)
                        Text("by ${p.authorName} • ${df.format(Date(p.createdAt))}", fontSize = 13.sp, color = Charcoal)
                        Spacer(Modifier.height(16.dp))
                        Text(p.content, fontSize = 15.sp, lineHeight = 22.sp)
                        Spacer(Modifier.height(16.dp))
                        HorizontalDivider()
                        Spacer(Modifier.height(16.dp))
                        Text("Comments (${p.repliesCount})", fontWeight = FontWeight.Bold, fontSize = 18.sp)
                    }
                }
                items(comments) { comment ->
                    Card(shape = RoundedCornerShape(12.dp), colors = CardDefaults.cardColors(containerColor = PaperWarm.copy(alpha = 0.5f))) {
                        Column(Modifier.padding(12.dp).fillMaxWidth()) {
                            Row(horizontalArrangement = Arrangement.SpaceBetween, modifier = Modifier.fillMaxWidth()) {
                                Text(comment.authorName, fontWeight = FontWeight.Bold, fontSize = 13.sp)
                                Text(df.format(Date(comment.createdAt)), fontSize = 11.sp, color = Charcoal)
                            }
                            Spacer(Modifier.height(4.dp))
                            Text(comment.content, fontSize = 14.sp)
                        }
                    }
                }
            } ?: item { Box(Modifier.fillMaxSize(), contentAlignment = Alignment.Center) { CircularProgressIndicator() } }
        }
    }
}
