package com.example.ui.components

import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.NavigationBarItemDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.unit.dp
import com.example.ui.theme.FlathubBlue

@Composable
fun FlathubBottomNav(
    currentDestination: NavDestination,
    onDestinationSelect: (NavDestination) -> Unit,
    modifier: Modifier = Modifier
) {
    NavigationBar(
        modifier = modifier.testTag("flathub_bottom_nav"),
        containerColor = MaterialTheme.colorScheme.surface,
        tonalElevation = 6.dp
    ) {
        NavDestination.entries.forEach { destination ->
            val isSelected = destination == currentDestination
            NavigationBarItem(
                selected = isSelected,
                onClick = { onDestinationSelect(destination) },
                icon = {
                    Icon(
                        imageVector = if (isSelected) destination.selectedIcon else destination.icon,
                        contentDescription = destination.label
                    )
                },
                label = {
                    Text(text = destination.label)
                },
                colors = NavigationBarItemDefaults.colors(
                    selectedIconColor = FlathubBlue,
                    selectedTextColor = FlathubBlue,
                    indicatorColor = FlathubBlue.copy(alpha = 0.15f),
                    unselectedIconColor = MaterialTheme.colorScheme.onSurfaceVariant,
                    unselectedTextColor = MaterialTheme.colorScheme.onSurfaceVariant
                )
            )
        }
    }
}
