package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.DarkMode
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material.icons.filled.Info
import androidx.compose.material.icons.filled.LightMode
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.outlined.Bookmarks
import androidx.compose.material.icons.outlined.Explore
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.ui.theme.AppTheme
import com.example.ui.theme.FlathubBlue

enum class NavDestination(val label: String, val icon: ImageVector, val selectedIcon: ImageVector) {
    EXPLORE("Explore", Icons.Outlined.Explore, Icons.Filled.Explore),
    LIBRARY("Library", Icons.Outlined.Bookmarks, Icons.Filled.Bookmarks),
    SEARCH("Search", Icons.Outlined.Search, Icons.Filled.Search)
}

@Composable
fun FlathubTopBar(
    currentDestination: NavDestination,
    onDestinationSelect: (NavDestination) -> Unit,
    currentTheme: AppTheme,
    onToggleTheme: () -> Unit,
    isTablet: Boolean,
    modifier: Modifier = Modifier
) {
    var showInfoDialog by remember { mutableStateOf(false) }

    Surface(
        modifier = modifier.fillMaxWidth(),
        color = MaterialTheme.colorScheme.background,
        tonalElevation = 0.5.dp
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .statusBarsPadding()
                .padding(horizontal = if (isTablet) 24.dp else 16.dp, vertical = 10.dp)
                .testTag("flathub_top_bar"),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.SpaceBetween
        ) {
            // Left: Flathub Branding
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.clickable { onDestinationSelect(NavDestination.EXPLORE) }
            ) {
                Box(
                    modifier = Modifier
                        .size(36.dp)
                        .clip(RoundedCornerShape(10.dp))
                        .background(FlathubBlue),
                    contentAlignment = Alignment.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Explore,
                        contentDescription = "Flathub Logo",
                        tint = Color.White,
                        modifier = Modifier.size(22.dp)
                    )
                }

                Spacer(modifier = Modifier.width(10.dp))

                Text(
                    text = "Flathub",
                    fontSize = 20.sp,
                    fontWeight = FontWeight.Bold,
                    color = MaterialTheme.colorScheme.onBackground
                )
            }

            // Center: Navigation Tabs (Shown on tablets and wide screens)
            if (isTablet) {
                Surface(
                    shape = RoundedCornerShape(24.dp),
                    color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.7f),
                    modifier = Modifier.padding(horizontal = 8.dp)
                ) {
                    Row(
                        modifier = Modifier.padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        NavDestination.entries.forEach { destination ->
                            val isSelected = destination == currentDestination

                            Box(
                                modifier = Modifier
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(
                                        if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                                    )
                                    .clickable { onDestinationSelect(destination) }
                                    .padding(horizontal = 20.dp, vertical = 8.dp),
                                contentAlignment = Alignment.Center
                            ) {
                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Icon(
                                        imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                                        contentDescription = destination.label,
                                        tint = if (isSelected) FlathubBlue else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
                                        modifier = Modifier.size(17.dp)
                                    )
                                    Spacer(modifier = Modifier.width(6.dp))
                                    Text(
                                        text = destination.label,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.SemiBold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                                    )
                                }
                            }
                        }
                    }
                }
            }

            // Right: Theme Toggle & Info Dialog (High Contrast in both Light and Dark Themes)
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                // Theme Toggle Button (Light / Dark)
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 1.dp
                ) {
                    IconButton(
                        onClick = onToggleTheme,
                        modifier = Modifier
                            .size(40.dp)
                            .testTag("theme_toggle_button")
                    ) {
                        Icon(
                            imageVector = if (currentTheme == AppTheme.DARK) Icons.Default.LightMode else Icons.Default.DarkMode,
                            contentDescription = if (currentTheme == AppTheme.DARK) "Switch to Light Theme" else "Switch to Dark Theme",
                            tint = if (currentTheme == AppTheme.DARK) Color(0xFFFFC107) else Color(0xFF1E293B)
                        )
                    }
                }

                // Info Dialog Button
                Surface(
                    shape = CircleShape,
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shadowElevation = 1.dp
                ) {
                    IconButton(
                        onClick = { showInfoDialog = true },
                        modifier = Modifier.size(40.dp)
                    ) {
                        Icon(
                            imageVector = Icons.Default.Info,
                            contentDescription = "About Flathub Client",
                            tint = MaterialTheme.colorScheme.onSurface
                        )
                    }
                }
            }
        }
    }

    if (showInfoDialog) {
        AlertDialog(
            onDismissRequest = { showInfoDialog = false },
            title = {
                Row(verticalAlignment = Alignment.CenterVertically) {
                    Icon(Icons.Default.Explore, contentDescription = null, tint = FlathubBlue)
                    Spacer(modifier = Modifier.width(8.dp))
                    Text("Flathub Client")
                }
            },
            text = {
                Text(
                    "Browse and search Linux Flatpak applications from Flathub directly on your Android phone and tablet.\n\n" +
                            "• Download .flatpakref files directly to your device.\n" +
                            "• Copy Flathub links to send to your Linux computer.\n" +
                            "• Copy terminal install commands (`flatpak install flathub <app>`).\n" +
                            "• Bookmark apps into your personal Library for offline reference.\n\n" +
                            "Current Theme: ${currentTheme.label} Mode (Tap the sun/moon icon to toggle)."
                )
            },
            confirmButton = {
                TextButton(onClick = { showInfoDialog = false }) {
                    Text("Got it")
                }
            }
        )
    }
}
