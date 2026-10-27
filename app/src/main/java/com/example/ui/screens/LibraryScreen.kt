package com.example.ui.screens

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Bookmarks
import androidx.compose.material.icons.filled.Explore
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FlathubRepository
import com.example.data.api.AppSummaryHit
import com.example.ui.components.AppCard
import com.example.ui.theme.FlathubBlue
import kotlinx.coroutines.launch

@Composable
fun LibraryScreen(
    repository: FlathubRepository,
    onAppClick: (String) -> Unit,
    onExploreClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val bookmarks by repository.getBookmarkedApps().collectAsState(initial = emptyList())

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("library_screen")
    ) {
        val screenWidth = maxWidth
        val isTablet = screenWidth >= 680.dp
        val columns = when {
            screenWidth >= 1080.dp -> 4
            screenWidth >= 720.dp -> 3
            screenWidth >= 500.dp -> 2
            else -> 1
        }

        if (bookmarks.isEmpty()) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(24.dp),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Icon(
                        imageVector = Icons.Default.Bookmarks,
                        contentDescription = null,
                        tint = FlathubBlue.copy(alpha = 0.6f),
                        modifier = Modifier.size(64.dp)
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    Text(
                        text = "Your Library is Empty",
                        fontSize = 20.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onSurface
                    )
                    Spacer(modifier = Modifier.height(8.dp))
                    Text(
                        text = "Save apps while exploring Flathub to easily download their .flatpakref files or copy their links to send to your Linux PC.",
                        fontSize = 14.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant,
                        modifier = Modifier.padding(horizontal = 24.dp),
                        lineHeight = 20.sp
                    )
                    Spacer(modifier = Modifier.height(24.dp))
                    Button(
                        onClick = onExploreClick,
                        colors = ButtonDefaults.buttonColors(containerColor = FlathubBlue),
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Icon(Icons.Default.Explore, contentDescription = null, modifier = Modifier.size(18.dp))
                        Spacer(modifier = Modifier.width(8.dp))
                        Text("Explore Apps")
                    }
                }
            }
        } else {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(horizontal = if (isTablet) 24.dp else 16.dp, vertical = 8.dp)
            ) {
                Row(
                    modifier = Modifier.fillMaxWidth().padding(vertical = 8.dp),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Saved Applications (${bookmarks.size})",
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Bold,
                        color = MaterialTheme.colorScheme.onBackground
                    )
                }

                val convertedHits = bookmarks.map { item ->
                    AppSummaryHit(
                        appId = item.appId,
                        name = item.name,
                        summary = item.summary,
                        icon = item.icon,
                        rawMainCategories = item.category?.let { listOf(it) },
                        verificationVerified = item.verified
                    )
                }

                val chunkedHits = convertedHits.chunked(columns)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(chunkedHits.size) { rowIndex ->
                        val rowItems = chunkedHits[rowIndex]
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(14.dp)
                        ) {
                            for (app in rowItems) {
                                Box(modifier = Modifier.weight(1f)) {
                                    AppCard(
                                        app = app,
                                        isBookmarked = true,
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
                            for (i in 0 until (columns - rowItems.size)) {
                                Spacer(modifier = Modifier.weight(1f))
                            }
                        }
                    }
                }
            }
        }
    }
}
