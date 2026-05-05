package com.example.hobbyhive.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.Logout
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.hobbyhive.ui.theme.GradientEnd
import com.example.hobbyhive.ui.theme.GradientStart

// ═══════════════════════════════════════════════════
// DrawerContent — ModalNavigationDrawer content
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

    ModalDrawerSheet(modifier = modifier) {
        // Header
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .height(180.dp)
                .padding(bottom = 8.dp),
            contentAlignment = Alignment.BottomStart
        ) {
            // Gradient overlay
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .background(
                        brush = Brush.verticalGradient(
                            colors = listOf(GradientStart, GradientEnd)
                        )
                    )
            )

            Column(
                modifier = Modifier
                    .padding(horizontal = 24.dp, vertical = 16.dp)
            ) {
                androidx.compose.foundation.Image(
                    painter = androidx.compose.ui.res.painterResource(id = com.example.hobbyhive.R.drawable.logo),
                    contentDescription = "HobbyHive Logo",
                    modifier = Modifier.size(48.dp).clip(RoundedCornerShape(12.dp))
                )
                Spacer(modifier = Modifier.height(8.dp))
                Text(
                    text = "HobbyHive",
                    style = MaterialTheme.typography.headlineSmall,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.inverseOnSurface
                )
                Text(
                    text = "Find Your Passion. Track Your Progress.",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.inverseOnSurface.copy(alpha = 0.7f)
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
                        contentDescription = item.label
                    )
                },
                label = {
                    Text(
                        text = item.label,
                        style = MaterialTheme.typography.labelLarge
                    )
                },
                selected = false,
                onClick = { onItemClick(item.route) },
                modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
            )
        }

        Spacer(modifier = Modifier.weight(1f))

        HorizontalDivider(modifier = Modifier.padding(horizontal = 24.dp))

        // Logout
        NavigationDrawerItem(
            icon = {
                Icon(
                    imageVector = Icons.AutoMirrored.Filled.Logout,
                    contentDescription = "Logout",
                    tint = MaterialTheme.colorScheme.error
                )
            },
            label = {
                Text(
                    text = "Logout",
                    color = MaterialTheme.colorScheme.error,
                    style = MaterialTheme.typography.labelLarge
                )
            },
            selected = false,
            onClick = onLogout,
            modifier = Modifier.padding(NavigationDrawerItemDefaults.ItemPadding)
        )

        Spacer(modifier = Modifier.height(16.dp))
    }
}
