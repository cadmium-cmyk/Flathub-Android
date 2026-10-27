package com.example.ui.screens

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Language
import androidx.compose.material.icons.filled.OpenInBrowser
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Divider
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilledTonalButton
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
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
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.FlathubRepository
import com.example.data.api.AppstreamDetail
import com.example.ui.components.ScreenshotViewerDialog
import com.example.ui.theme.FlathubBlue
import com.example.ui.theme.FlathubBlueDark
import com.example.util.FlatpakHelper
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AppDetailScreen(
    appId: String,
    repository: FlathubRepository,
    onBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    BackHandler { onBack() }

    val context = LocalContext.current
    val scope = rememberCoroutineScope()

    var detail by remember { mutableStateOf<AppstreamDetail?>(null) }
    var isLoading by remember { mutableStateOf(true) }
    var errorMessage by remember { mutableStateOf<String?>(null) }
    var selectedScreenshot by remember { mutableStateOf<String?>(null) }

    val isBookmarked by repository.isBookmarked(appId).collectAsState(initial = false)

    fun loadDetails() {
        scope.launch {
            isLoading = true
            errorMessage = null
            try {
                detail = repository.getAppDetails(appId)
            } catch (e: Exception) {
                e.printStackTrace()
                errorMessage = e.localizedMessage ?: "Failed to load app details"
            } finally {
                isLoading = false
            }
        }
    }

    LaunchedEffect(appId) {
        loadDetails()
    }

    Scaffold(
        modifier = modifier.fillMaxSize().testTag("app_detail_screen"),
        topBar = {
            TopAppBar(
                title = {
                    Text(
                        text = detail?.name ?: appId,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.SemiBold
                    )
                },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                },
                actions = {
                    // Bookmark toggle
                    IconButton(
                        onClick = {
                            val current = detail
                            if (current != null) {
                                scope.launch {
                                    repository.toggleBookmark(
                                        appId = appId,
                                        name = current.name ?: appId,
                                        summary = current.summary.orEmpty(),
                                        icon = current.icon,
                                        category = current.categories?.firstOrNull(),
                                        verified = true
                                    )
                                }
                            }
                        }
                    ) {
                        Icon(
                            imageVector = if (isBookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                            contentDescription = if (isBookmarked) "In Library" else "Save to Library",
                            tint = if (isBookmarked) FlathubBlue else MaterialTheme.colorScheme.onSurface
                        )
                    }

                    // Share
                    IconButton(
                        onClick = {
                            FlatpakHelper.shareApp(context, appId, detail?.name ?: appId)
                        }
                    ) {
                        Icon(Icons.Default.Share, contentDescription = "Send to device")
                    }

                    // Open on Web
                    IconButton(
                        onClick = {
                            FlatpakHelper.openInBrowser(context, FlatpakHelper.getFlathubWebUrl(appId))
                        }
                    ) {
                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = "View on Flathub Web")
                    }
                },
                colors = TopAppBarDefaults.topAppBarColors(
                    containerColor = MaterialTheme.colorScheme.surface
                )
            )
        }
    ) { innerPadding ->
        if (isLoading) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                CircularProgressIndicator(color = FlathubBlue)
            }
        } else if (errorMessage != null || detail == null) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding),
                contentAlignment = Alignment.Center
            ) {
                Column(horizontalAlignment = Alignment.CenterHorizontally) {
                    Text(text = errorMessage ?: "Application details unavailable", fontSize = 16.sp)
                    Spacer(modifier = Modifier.height(16.dp))
                    Button(onClick = { loadDetails() }) {
                        Icon(Icons.Default.Refresh, contentDescription = null, modifier = Modifier.size(16.dp))
                        Spacer(modifier = Modifier.width(6.dp))
                        Text("Retry")
                    }
                }
            }
        } else {
            val app = detail!!

            BoxWithConstraints(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(innerPadding)
            ) {
                val isTablet = maxWidth >= 680.dp

                LazyColumn(
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(horizontal = if (isTablet) 32.dp else 16.dp, vertical = 16.dp),
                    verticalArrangement = Arrangement.spacedBy(20.dp)
                ) {
                    // 1. App Header Info
                    item {
                        Row(
                            modifier = Modifier.fillMaxWidth(),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            // Icon
                            Box(
                                modifier = Modifier
                                    .size(if (isTablet) 96.dp else 76.dp)
                                    .clip(RoundedCornerShape(20.dp))
                                    .background(MaterialTheme.colorScheme.surfaceVariant)
                                    .border(
                                        width = 1.dp,
                                        color = MaterialTheme.colorScheme.outline.copy(alpha = 0.3f),
                                        shape = RoundedCornerShape(20.dp)
                                    ),
                                contentAlignment = Alignment.Center
                            ) {
                                if (!app.icon.isNullOrBlank()) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(app.icon)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = app.name,
                                        contentScale = ContentScale.Fit,
                                        modifier = Modifier.size(if (isTablet) 86.dp else 68.dp)
                                    )
                                } else {
                                    Text(
                                        text = (app.name ?: "A").take(1),
                                        fontSize = 32.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = FlathubBlue
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.width(18.dp))

                            Column(modifier = Modifier.weight(1f)) {
                                Text(
                                    text = app.name ?: appId,
                                    fontSize = if (isTablet) 28.sp else 22.sp,
                                    fontWeight = FontWeight.Bold,
                                    color = MaterialTheme.colorScheme.onSurface
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Row(verticalAlignment = Alignment.CenterVertically) {
                                    Text(
                                        text = app.developerName ?: "Verified Flatpak",
                                        fontSize = 14.sp,
                                        color = MaterialTheme.colorScheme.onSurfaceVariant
                                    )
                                    Spacer(modifier = Modifier.width(4.dp))
                                    Icon(
                                        imageVector = Icons.Default.CheckCircle,
                                        contentDescription = "Verified Publisher",
                                        tint = FlathubBlue,
                                        modifier = Modifier.size(15.dp)
                                    )
                                }

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = appId,
                                    fontSize = 12.sp,
                                    fontFamily = FontFamily.Monospace,
                                    color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.7f)
                                )
                            }
                        }
                    }

                    // 2. Summary
                    if (!app.summary.isNullOrBlank()) {
                        item {
                            Text(
                                text = app.summary,
                                fontSize = 16.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurfaceVariant
                            )
                        }
                    }

                    // 3. Primary Action Buttons (Download .flatpakref, Copy Link, Send to Device)
                    item {
                        Column(
                            modifier = Modifier.fillMaxWidth(),
                            verticalArrangement = Arrangement.spacedBy(10.dp)
                        ) {
                            Row(
                                modifier = Modifier.fillMaxWidth(),
                                horizontalArrangement = Arrangement.spacedBy(10.dp)
                            ) {
                                // Download .flatpakref
                                Button(
                                    onClick = {
                                        FlatpakHelper.downloadFlatpakref(context, appId, app.name ?: appId)
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    colors = ButtonDefaults.buttonColors(containerColor = FlathubBlue),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.Download, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Download .flatpakref", fontWeight = FontWeight.SemiBold)
                                }

                                // Copy Link
                                FilledTonalButton(
                                    onClick = {
                                        FlatpakHelper.copyFlathubLink(context, appId)
                                    },
                                    modifier = Modifier.weight(1f).height(48.dp),
                                    shape = RoundedCornerShape(12.dp)
                                ) {
                                    Icon(Icons.Default.ContentCopy, contentDescription = null, modifier = Modifier.size(18.dp))
                                    Spacer(modifier = Modifier.width(8.dp))
                                    Text("Copy Link")
                                }
                            }

                            // Share / Send to Device Button
                            OutlinedButton(
                                onClick = {
                                    FlatpakHelper.shareApp(context, appId, app.name ?: appId)
                                },
                                modifier = Modifier.fillMaxWidth().height(46.dp),
                                shape = RoundedCornerShape(12.dp)
                            ) {
                                Icon(Icons.Default.Share, contentDescription = null, modifier = Modifier.size(18.dp))
                                Spacer(modifier = Modifier.width(8.dp))
                                Text("Send Link & Info to Linux Device")
                            }
                        }
                    }

                    // 4. Terminal Command Card
                    item {
                        Card(
                            shape = RoundedCornerShape(16.dp),
                            colors = CardDefaults.cardColors(
                                containerColor = MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f)
                            ),
                            border = CardDefaults.outlinedCardBorder()
                        ) {
                            Column(modifier = Modifier.fillMaxWidth().padding(16.dp)) {
                                Row(
                                    modifier = Modifier.fillMaxWidth(),
                                    verticalAlignment = Alignment.CenterVertically,
                                    horizontalArrangement = Arrangement.SpaceBetween
                                ) {
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Icon(
                                            imageVector = Icons.Default.Terminal,
                                            contentDescription = null,
                                            tint = FlathubBlue,
                                            modifier = Modifier.size(18.dp)
                                        )
                                        Spacer(modifier = Modifier.width(8.dp))
                                        Text(
                                            text = "Linux Terminal Install",
                                            fontSize = 13.sp,
                                            fontWeight = FontWeight.SemiBold,
                                            color = MaterialTheme.colorScheme.onSurface
                                        )
                                    }

                                    IconButton(
                                        onClick = { FlatpakHelper.copyInstallCommand(context, appId) },
                                        modifier = Modifier.size(32.dp)
                                    ) {
                                        Icon(
                                            imageVector = Icons.Default.ContentCopy,
                                            contentDescription = "Copy install command",
                                            modifier = Modifier.size(16.dp)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(8.dp))

                                Surface(
                                    color = Color(0xFF1E2538),
                                    shape = RoundedCornerShape(8.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { FlatpakHelper.copyInstallCommand(context, appId) }
                                ) {
                                    Text(
                                        text = FlatpakHelper.getInstallCommand(appId),
                                        fontFamily = FontFamily.Monospace,
                                        fontSize = 13.sp,
                                        color = Color(0xFF78AEED),
                                        modifier = Modifier.padding(12.dp)
                                    )
                                }
                            }
                        }
                    }

                    // 5. Screenshots Gallery
                    val screenshots = app.bestScreenshots
                    if (screenshots.isNotEmpty()) {
                        item {
                            Text(
                                text = "Screenshots",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(10.dp))

                            Row(
                                modifier = Modifier
                                    .fillMaxWidth()
                                    .horizontalScroll(rememberScrollState()),
                                horizontalArrangement = Arrangement.spacedBy(12.dp)
                            ) {
                                screenshots.forEach { url ->
                                    Card(
                                        shape = RoundedCornerShape(12.dp),
                                        modifier = Modifier
                                            .width(if (isTablet) 420.dp else 290.dp)
                                            .aspectRatio(16f / 10f)
                                            .clip(RoundedCornerShape(12.dp))
                                            .clickable { selectedScreenshot = url },
                                        elevation = CardDefaults.cardElevation(defaultElevation = 1.dp)
                                    ) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(url)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "App Screenshot",
                                            contentScale = ContentScale.Crop,
                                            modifier = Modifier.fillMaxSize()
                                        )
                                    }
                                }
                            }
                        }
                    }

                    // 6. Full Description
                    if (!app.description.isNullOrBlank()) {
                        item {
                            Text(
                                text = "About this application",
                                fontSize = 18.sp,
                                fontWeight = FontWeight.Bold,
                                color = MaterialTheme.colorScheme.onBackground
                            )

                            Spacer(modifier = Modifier.height(8.dp))

                            val cleanDescription = remember(app.description) {
                                app.description
                                    .replace(Regex("<br\\s*/?>"), "\n")
                                    .replace(Regex("<li>"), "• ")
                                    .replace(Regex("</li>"), "\n")
                                    .replace(Regex("<[^>]*>"), "")
                                    .trim()
                            }

                            Text(
                                text = cleanDescription,
                                fontSize = 14.sp,
                                lineHeight = 22.sp,
                                color = MaterialTheme.colorScheme.onSurface
                            )
                        }
                    }

                    // 7. Metadata / Specifications
                    item {
                        HorizontalDivider(modifier = Modifier.padding(vertical = 4.dp))

                        Text(
                            text = "Additional Information",
                            fontSize = 18.sp,
                            fontWeight = FontWeight.Bold,
                            color = MaterialTheme.colorScheme.onBackground
                        )

                        Spacer(modifier = Modifier.height(12.dp))

                        Column(verticalArrangement = Arrangement.spacedBy(10.dp)) {
                            // License
                            if (!app.projectLicense.isNullOrBlank()) {
                                MetadataRow(label = "License", value = app.projectLicense)
                            }

                            // Categories
                            if (!app.categories.isNullOrEmpty()) {
                                MetadataRow(label = "Categories", value = app.categories.joinToString(", "))
                            }

                            // Latest Version
                            val latestRelease = app.releases?.firstOrNull()
                            if (latestRelease?.version != null) {
                                MetadataRow(label = "Version", value = latestRelease.version)
                            }

                            // Project links
                            app.urls?.get("homepage")?.let { url ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { FlatpakHelper.openInBrowser(context, url) }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Developer Website", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Row(verticalAlignment = Alignment.CenterVertically) {
                                        Text("View", fontSize = 14.sp, color = FlathubBlue)
                                        Spacer(modifier = Modifier.width(4.dp))
                                        Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null, tint = FlathubBlue, modifier = Modifier.size(16.dp))
                                    }
                                }
                            }

                            app.urls?.get("bugtracker")?.let { url ->
                                Row(
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .clickable { FlatpakHelper.openInBrowser(context, url) }
                                        .padding(vertical = 4.dp),
                                    horizontalArrangement = Arrangement.SpaceBetween,
                                    verticalAlignment = Alignment.CenterVertically
                                ) {
                                    Text("Bug Tracker", fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
                                    Text("Open Issues", fontSize = 14.sp, color = FlathubBlue)
                                }
                            }
                        }

                        Spacer(modifier = Modifier.height(24.dp))
                    }
                }
            }
        }
    }

    // Full screen screenshot dialog
    selectedScreenshot?.let { url ->
        ScreenshotViewerDialog(
            screenshotUrl = url,
            onDismiss = { selectedScreenshot = null }
        )
    }
}

@Composable
fun MetadataRow(label: String, value: String) {
    Row(
        modifier = Modifier.fillMaxWidth().padding(vertical = 4.dp),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically
    ) {
        Text(text = label, fontSize = 14.sp, color = MaterialTheme.colorScheme.onSurfaceVariant)
        Text(text = value, fontSize = 14.sp, fontWeight = FontWeight.Medium, color = MaterialTheme.colorScheme.onSurface)
    }
}
