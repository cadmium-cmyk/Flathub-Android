package com.example.ui.screens

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.SuggestionChip
import androidx.compose.material3.SuggestionChipDefaults
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
import androidx.compose.ui.platform.LocalSoftwareKeyboardController
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.example.data.FlathubRepository
import com.example.data.api.AppSummaryHit
import com.example.ui.components.AppCard
import com.example.ui.theme.FlathubBlue
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

@Composable
fun SearchScreen(
    repository: FlathubRepository,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    val scope = rememberCoroutineScope()
    val keyboardController = LocalSoftwareKeyboardController.current

    var searchQuery by remember { mutableStateOf("") }
    var searchResults by remember { mutableStateOf<List<AppSummaryHit>>(emptyList()) }
    var isSearching by remember { mutableStateOf(false) }
    var searchJob by remember { mutableStateOf<Job?>(null) }
    var hasSearched by remember { mutableStateOf(false) }

    val bookmarks by repository.getBookmarkedApps().collectAsState(initial = emptyList())
    val bookmarkedIds = remember(bookmarks) { bookmarks.map { it.appId }.toSet() }

    val popularSuggestions = listOf(
        "Firefox", "VLC", "Blender", "GIMP", "Obsidian",
        "Steam", "Discord", "LibreOffice", "Krita", "VS Code"
    )

    fun executeSearch(query: String) {
        searchJob?.cancel()
        if (query.isBlank()) {
            searchResults = emptyList()
            isSearching = false
            hasSearched = false
            return
        }

        searchJob = scope.launch {
            delay(300) // Debounce
            isSearching = true
            hasSearched = true
            try {
                searchResults = repository.search(query.trim())
            } catch (e: Exception) {
                e.printStackTrace()
            } finally {
                isSearching = false
            }
        }
    }

    BoxWithConstraints(
        modifier = modifier
            .fillMaxSize()
            .testTag("search_screen")
    ) {
        val screenWidth = maxWidth
        val isTablet = screenWidth >= 680.dp
        val columns = when {
            screenWidth >= 1080.dp -> 4
            screenWidth >= 720.dp -> 3
            screenWidth >= 500.dp -> 2
            else -> 1
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = if (isTablet) 24.dp else 16.dp, vertical = 8.dp)
        ) {
            // Search Input Bar
            OutlinedTextField(
                value = searchQuery,
                onValueChange = {
                    searchQuery = it
                    executeSearch(it)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .testTag("search_input"),
                placeholder = {
                    Text("Search apps, utilities, tools, games on Flathub...")
                },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Default.Search,
                        contentDescription = "Search",
                        tint = FlathubBlue
                    )
                },
                trailingIcon = {
                    if (searchQuery.isNotEmpty()) {
                        IconButton(onClick = {
                            searchQuery = ""
                            searchResults = emptyList()
                            hasSearched = false
                        }) {
                            Icon(Icons.Default.Clear, contentDescription = "Clear search")
                        }
                    }
                },
                singleLine = true,
                shape = RoundedCornerShape(20.dp),
                colors = OutlinedTextFieldDefaults.colors(
                    focusedBorderColor = FlathubBlue,
                    unfocusedBorderColor = MaterialTheme.colorScheme.outline.copy(alpha = 0.5f),
                    focusedContainerColor = MaterialTheme.colorScheme.surface,
                    unfocusedContainerColor = MaterialTheme.colorScheme.surface
                ),
                keyboardOptions = KeyboardOptions(imeAction = ImeAction.Search),
                keyboardActions = KeyboardActions(onSearch = {
                    keyboardController?.hide()
                    executeSearch(searchQuery)
                })
            )

            // Popular Suggestions Chips
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .horizontalScroll(rememberScrollState())
                    .padding(vertical = 10.dp),
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                popularSuggestions.forEach { suggestion ->
                    SuggestionChip(
                        onClick = {
                            searchQuery = suggestion
                            keyboardController?.hide()
                            executeSearch(suggestion)
                        },
                        label = { Text(suggestion, fontSize = 12.sp) },
                        colors = SuggestionChipDefaults.suggestionChipColors(
                            containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                        ),
                        border = SuggestionChipDefaults.suggestionChipBorder(enabled = false)
                    )
                }
            }

            // Results Section
            if (isSearching) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator(color = FlathubBlue)
                }
            } else if (hasSearched && searchResults.isEmpty()) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text(
                            text = "No applications found for \"$searchQuery\"",
                            fontSize = 16.sp,
                            fontWeight = FontWeight.Medium,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Try searching by package name or general keywords like 'video editor' or 'browser'",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else if (!hasSearched) {
                // Empty state before searching
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f),
                    contentAlignment = Alignment.Center
                ) {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Icon(
                            imageVector = Icons.Default.Search,
                            contentDescription = null,
                            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.5f),
                            modifier = Modifier.size(64.dp)
                        )
                        Spacer(modifier = Modifier.height(12.dp))
                        Text(
                            text = "Search Flathub",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.SemiBold,
                            color = MaterialTheme.colorScheme.onSurface
                        )
                        Spacer(modifier = Modifier.height(4.dp))
                        Text(
                            text = "Find over 2,500 desktop apps and tools packaged for Linux",
                            fontSize = 13.sp,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            } else {
                // Results List
                Text(
                    text = "${searchResults.size} results for \"$searchQuery\"",
                    fontSize = 14.sp,
                    fontWeight = FontWeight.Medium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.padding(vertical = 8.dp)
                )

                val chunkedResults = searchResults.chunked(columns)
                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(14.dp),
                    contentPadding = PaddingValues(bottom = 24.dp)
                ) {
                    items(chunkedResults.size) { rowIndex ->
                        val rowItems = chunkedResults[rowIndex]
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
