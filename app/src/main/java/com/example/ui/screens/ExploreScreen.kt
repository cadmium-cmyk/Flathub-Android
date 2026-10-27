package com.example.ui.screens

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Warning
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FlathubRepository
import com.example.data.api.AppSummaryHit
import com.example.data.api.AppstreamDetail
import com.example.ui.components.AppCard
import com.example.ui.components.HeroBanner
import com.example.ui.theme.FlathubBlue
import kotlinx.coroutines.launch

enum class ExploreTab(val title: String) {
    TRENDING("Trending"),
    POPULAR("Popular"),
    NEW("New"),
    UPDATED("Updated")
}

@Composable
fun ExploreScreen(
    repository: FlathubRepository,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    var selectedTab by remember { mutableStateOf(ExploreTab.TRENDING) }
    var selectedCategory by remember { mutableStateOf<String?>(null) }

    var heroApps by remember { mutableStateOf<List<AppstreamDetail>>(emptyList()) }
    var currentApps by remember { mutableStateOf<List<AppSummaryHit>>(emptyList()) }
    var productivityApps by remember { mutableStateOf<List<AppSummaryHit>>(emptyList()) }
    var audioVideoApps by remember { mutableStateOf<List<AppSummaryHit>>(emptyList()) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }

    val bookmarks by repository.getBookmarkedApps().collectAsState(initial = emptyList())
    val bookmarkedIds = remember(bookmarks) { bookmarks.map { it.appId }.toSet() }

    // Load data
    fun loadData(tab: ExploreTab = selectedTab, category: String? = selectedCategory) {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                if (heroApps.isEmpty()) {
                    heroApps = repository.getFeaturedHeroApps()
                }

                currentApps = if (category != null) {
                    repository.getCategoryApps(category)
                } else {
                    when (tab) {
                        ExploreTab.TRENDING -> repository.getTrending()
                        ExploreTab.POPULAR -> repository.getPopular()
                        ExploreTab.NEW -> repository.getRecentlyAdded()
                        ExploreTab.UPDATED -> repository.getRecentlyUpdated()
                    }
                }

                // Load category shelves for explore view if no category is picked
                if (category == null) {
                    if (productivityApps.isEmpty()) {
                        productivityApps = repository.getCategoryApps("office")
                    }
                    if (audioVideoApps.isEmpty()) {
                        audioVideoApps = repository.getCategoryApps("audiovideo")
                    }
                }
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.localizedMessage ?: "Failed to connect to Flathub. Please check your internet connection."
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(selectedTab, selectedCategory) {
        loadData(selectedTab, selectedCategory)
    }

    BoxWithConstraints(modifier = modifier.fillMaxSize().testTag("explore_screen")) {
        val screenWidth = maxWidth
        val isTablet = screenWidth >= 680.dp
        val columns = when {
            screenWidth >= 1080.dp -> 4
            screenWidth >= 720.dp -> 3
            screenWidth >= 500.dp -> 2
            else -> 1
        }

        LazyColumn(
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(horizontal = if (isTablet) 24.dp else 16.dp, vertical = 12.dp),
            verticalArrangement = Arrangement.spacedBy(16.dp)
        ) {
            // 1. Featured Hero Banner
            if (heroApps.isNotEmpty()) {
                item {
                    HeroBanner(
                        featuredApps = heroApps,
                        onAppClick = onAppClick,
                        modifier = Modifier.padding(bottom = 8.dp)
                    )
                }
            }

            // 2. Section Pill Filter Row (Trending | Popular | New | Updated) - exactly like the screenshot!
            item {
                Column(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalAlignment = Alignment.CenterHorizontally
                ) {
                    Surface(
                        shape = RoundedCornerShape(24.dp),
                        color = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.6f),
                        modifier = Modifier.padding(vertical = 4.dp)
                    ) {
                        Row(
                            modifier = Modifier
                                .horizontalScroll(rememberScrollState())
                                .padding(4.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            ExploreTab.entries.forEach { tab ->
                                val isSelected = selectedTab == tab && selectedCategory == null

                                Box(
                                    modifier = Modifier
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(
                                            if (isSelected) MaterialTheme.colorScheme.surface else Color.Transparent
                                        )
                                        .clickable {
                                            selectedCategory = null
                                            selectedTab = tab
                                        }
                                        .padding(horizontal = if (isTablet) 24.dp else 16.dp, vertical = 8.dp),
                                    contentAlignment = Alignment.Center
                                ) {
                                    Text(
                                        text = tab.title,
                                        fontSize = 14.sp,
                                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal,
                                        color = if (isSelected) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                }
                            }
                        }
                    }

                    // Category Quick Chips
                    val categories = listOf(
                        "All" to null,
                        "Productivity" to "office",
                        "Audio & Video" to "audiovideo",
                        "Graphics" to "graphics",
                        "Utilities" to "utility",
                        "Development" to "development",
                        "Games" to "game",
                        "System" to "system"
                    )

                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .horizontalScroll(rememberScrollState())
                            .padding(top = 10.dp, bottom = 4.dp),
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        categories.forEach { (label, slug) ->
                            val isSelected = selectedCategory == slug
                            FilterChip(
                                selected = isSelected,
                                onClick = {
                                    selectedCategory = slug
                                },
                                label = { Text(label, fontSize = 12.sp) },
                                colors = FilterChipDefaults.filterChipColors(
                                    selectedContainerColor = FlathubBlue.copy(alpha = 0.15f),
                                    selectedLabelColor = FlathubBlue
                                )
                            )
                        }
                    }
                }
            }

            // 3. Loading / Error / Empty States
            if (isLoading && currentApps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(240.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(horizontalAlignment = Alignment.CenterHorizontally) {
                            CircularProgressIndicator(color = FlathubBlue)
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = "Loading Flathub catalog...",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                fontSize = 14.sp
                            )
                        }
                    }
                }
            } else if (errorMessage != null && currentApps.isEmpty()) {
                item {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 40.dp),
                        contentAlignment = Alignment.Center
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Warning,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.error,
                                modifier = Modifier.size(48.dp)
                            )
                            Spacer(modifier = Modifier.height(12.dp))
                            Text(
                                text = errorMessage ?: "Failed to load applications",
                                color = MaterialTheme.colorScheme.onSurface,
                                fontSize = 15.sp
                            )
                            Spacer(modifier = Modifier.height(16.dp))
                            Button(
                                onClick = { loadData() },
                                colors = ButtonDefaults.buttonColors(containerColor = FlathubBlue)
                            ) {
                                Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                                Spacer(modifier = Modifier.width(6.dp))
                                Text("Retry")
                            }
                        }
                    }
                }
            } else {
                // 4. Main App Grid
                // In Compose LazyColumn, we can chunk the apps into rows of `columns` items
                val chunkedApps = currentApps.chunked(columns)
                items(chunkedApps.size) { rowIndex ->
                    val rowItems = chunkedApps[rowIndex]
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(14.dp)
                    ) {
                        for (app in rowItems) {
                            Box(modifier = Modifier.weight(1f)) {
                                val isBookmarked = bookmarkedIds.contains(app.cleanAppId)
                                AppCard(
                                    app = app,
                                    isBookmarked = isBookmarked,
                                    onAppClick = onAppClick,
                                    onBookmarkToggle = {
                                        scope.launch {
                                            repository.toggleBookmark(
                                                appId = app.cleanAppId,
                                                name = app.cleanName,
                                                summary = app.cleanSummary,
                                                icon = app.icon,
                                                category = app.mainCategories?.firstOrNull(),
                                                verified = app.verificationVerified == true
                                            )
                                        }
                                    }
                                )
                            }
                        }
                        // Fill empty cells if last row has fewer items
                        for (i in 0 until (columns - rowItems.size)) {
                            Spacer(modifier = Modifier.weight(1f))
                        }
                    }
                }

                // 5. Category Shelves (Like the "Productivity" section in the screenshot!)
                if (selectedCategory == null && productivityApps.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Productivity",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    val chunkedProductivity = productivityApps.take(8).chunked(columns)
                    items(chunkedProductivity.size) { rowIndex ->
                        val rowItems = chunkedProductivity[rowIndex]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            for (app in rowItems) {
                                Box(modifier = Modifier.weight(1f)) {
                                    val isBookmarked = bookmarkedIds.contains(app.cleanAppId)
                                    AppCard(
                                        app = app,
                                        isBookmarked = isBookmarked,
                                        onAppClick = onAppClick,
                                        onBookmarkToggle = {
                                            scope.launch {
                                                repository.toggleBookmark(
                                                    appId = app.cleanAppId,
                                                    name = app.cleanName,
                                                    summary = app.cleanSummary,
                                                    icon = app.icon,
                                                    category = "Productivity",
                                                    verified = app.verificationVerified == true
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                            for (i in 0 until (columns - rowItems.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // 6. Audio & Video Shelf
                if (selectedCategory == null && audioVideoApps.isNotEmpty()) {
                    item {
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Audio & Video",
                            fontSize = 22.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground,
                            modifier = Modifier.padding(vertical = 8.dp)
                        )
                    }

                    val chunkedAudioVideo = audioVideoApps.take(8).chunked(columns)
                    items(chunkedAudioVideo.size) { rowIndex ->
                        val rowItems = chunkedAudioVideo[rowIndex]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            for (app in rowItems) {
                                Box(modifier = Modifier.weight(1f)) {
                                    val isBookmarked = bookmarkedIds.contains(app.cleanAppId)
                                    AppCard(
                                        app = app,
                                        isBookmarked = isBookmarked,
                                        onAppClick = onAppClick,
                                        onBookmarkToggle = {
                                            scope.launch {
                                                repository.toggleBookmark(
                                                    appId = app.cleanAppId,
                                                    name = app.cleanName,
                                                    summary = app.cleanSummary,
                                                    icon = app.icon,
                                                    category = "Audio & Video",
                                                    verified = app.verificationVerified == true
                                                )
                                            }
                                        }
                                    )
                                }
                            }
                            for (i in 0 until (columns - rowItems.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }

                // Footer spacer
                item {
                    Spacer(modifier = Modifier.height(32.dp))
                }
            }
        }
    }
}
