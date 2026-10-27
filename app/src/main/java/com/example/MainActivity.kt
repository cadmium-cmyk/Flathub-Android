package com.example

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import com.example.data.FlathubRepository
import com.example.data.api.FlathubApiService
import com.example.data.local.AppDatabase
import com.example.ui.components.FlathubBottomNav
import com.example.ui.components.FlathubTopBar
import com.example.ui.components.NavDestination
import com.example.ui.screens.AppDetailScreen
import com.example.ui.screens.ExploreScreen
import com.example.ui.screens.LibraryScreen
import com.example.ui.screens.SearchScreen
import com.example.ui.theme.AppTheme
import com.example.ui.theme.MyApplicationTheme
import com.example.util.ThemePreferences

class MainActivity : ComponentActivity() {

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        val themePrefs = ThemePreferences(this)

        setContent {
            var currentTheme by remember { mutableStateOf(themePrefs.getTheme()) }

            MyApplicationTheme(appTheme = currentTheme) {
                FlathubAppRoot(
                    currentTheme = currentTheme,
                    onToggleTheme = {
                        val nextTheme = if (currentTheme == AppTheme.LIGHT) AppTheme.DARK else AppTheme.LIGHT
                        currentTheme = nextTheme
                        themePrefs.setTheme(nextTheme)
                    }
                )
            }
        }
    }
}

@Composable
fun FlathubAppRoot(
    currentTheme: AppTheme,
    onToggleTheme: () -> Unit
) {
    val context = LocalContext.current
    val repository = remember {
        val api = FlathubApiService.create()
        val db = AppDatabase.getInstance(context)
        FlathubRepository(api, db.bookmarkedAppDao())
    }

    var currentDestination by remember { mutableStateOf(NavDestination.EXPLORE) }
    var selectedAppId by remember { mutableStateOf<String?>(null) }

    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        val isTablet = maxWidth >= 680.dp

        if (selectedAppId != null) {
            AppDetailScreen(
                appId = selectedAppId!!,
                repository = repository,
                onBack = { selectedAppId = null }
            )
        } else {
            Scaffold(
                modifier = Modifier
                    .fillMaxSize()
                    .background(MaterialTheme.colorScheme.background),
                topBar = {
                    FlathubTopBar(
                        currentDestination = currentDestination,
                        onDestinationSelect = { destination ->
                            currentDestination = destination
                        },
                        currentTheme = currentTheme,
                        onToggleTheme = onToggleTheme,
                        isTablet = isTablet
                    )
                },
                bottomBar = {
                    if (!isTablet) {
                        FlathubBottomNav(
                            currentDestination = currentDestination,
                            onDestinationSelect = { destination ->
                                currentDestination = destination
                            }
                        )
                    }
                }
            ) { innerPadding ->
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(innerPadding)
                ) {
                    when (currentDestination) {
                        NavDestination.EXPLORE -> {
                            ExploreScreen(
                                repository = repository,
                                onAppClick = { appId -> selectedAppId = appId }
                            )
                        }
                        NavDestination.LIBRARY -> {
                            LibraryScreen(
                                repository = repository,
                                onAppClick = { appId -> selectedAppId = appId },
                                onExploreClick = { currentDestination = NavDestination.EXPLORE }
                            )
                        }
                        NavDestination.SEARCH -> {
                            SearchScreen(
                                repository = repository,
                                onAppClick = { appId -> selectedAppId = appId }
                            )
                        }
                    }
                }
            }
        }
    }
}
