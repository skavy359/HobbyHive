package com.example.hobbyhive.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.ui.draw.clip
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.hobbyhive.ui.theme.*

// ═══════════════════════════════════════════════════
// DrawerContent — Playful editorial nav drawer
// Honey header, sticker-style items, bee branding
// ═══════════════════════════════════════════════════

data class DrawerItem(
    val icon: ImageVector,
    val label: String,
    val route: String
)

@Composable
fun DrawerContent(
    onItemClick: (String) -> Unit,
    onLogout: () -> Unit,
    modifier: Modifier = Modifier
) {
    val items = listOf(
        DrawerItem(Icons.Default.Home, "Dashboard", "home"),
        DrawerItem(Icons.Default.Timer, "Sessions", "sessions"),
        DrawerItem(Icons.Default.Flag, "Goals", "goals"),
        DrawerItem(Icons.Default.CalendarMonth, "Planner", "planner"),
        DrawerItem(Icons.Default.Analytics, "Analytics", "analytics"),
        DrawerItem(Icons.Default.Person, "Profile", "profile"),
        DrawerItem(Icons.Default.EmojiEvents, "Leaderboard", "leaderboard"),
        DrawerItem(Icons.Default.Star, "Achievements", "achievements"),
        DrawerItem(Icons.Default.Category, "Browse Categories", "categoryBrowse"),
        DrawerItem(Icons.Default.Groups, "Community", "community"),
        DrawerItem(Icons.Default.Notifications, "Notifications", "notifications"),
        DrawerItem(Icons.Default.Settings, "Settings", "settings")
    )

    ModalDrawerSheet(
        modifier = modifier,
        drawerContainerColor = PaperCream
    ) {
        // Honey-themed header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            // Warm honey background
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(HoneyYellow)
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                // Logo placeholder — bee emoji as sticker
                Surface(
                    modifier = Modifier.size(48.dp),
                    shape = RoundedCornerShape(14.dp),
                    color = PaperWhite,
                    border = BorderStroke(2.dp, InkBlack)
                ) {
                    Box(contentAlignment = Alignment.Center) {
                        Text("🐝", fontSize = 24.sp)
                    }
                }
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "HobbyHive",
                    fontSize = 24.sp,
                    fontWeight = FontWeight.Black,
                    color = InkBlack
                )
                Text(
                    text = "Find Your Passion. Track Your Progress.",
                    fontSize = 12.sp,
                    fontWeight = FontWeight.SemiBold,
                    color = InkBlack.copy(alpha = 0.7f)
                )
            }
        }

        Spacer(modifier = Modifier.height(8.dp))

        // Navigation items
        items.forEach { item ->
            NavigationDrawerItem(
                icon = {
                    Icon(
                        imageVector = item.icon,
                        contentDescription = item.label,
                        tint = InkBlack
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        fontWeight = FontWeight.Bold,
                        color = InkBlack
                    )
                },
                selected = false,
                onClick = { onItemClick(item.route) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding),
                colors = NavigationDrawerItemDefaults.colors(
                    unselectedContainerColor = Color.Transparent,
                    selectedContainerColor = HoneyLight
                )
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(
            modifier = Modifier.padding(horizontal = 24.dp),
            color = InkBlack.copy(alpha = 0.15f),
            thickness = 2.dp
        )

        // Logout
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = ErrorRed
                )
            },
            label = {
                Text(
                    text = "Logout",
                    color = ErrorRed,
                    fontWeight = FontWeight.ExtraBold
                )
            },
            selected = false,
            onClick = onLogout,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
