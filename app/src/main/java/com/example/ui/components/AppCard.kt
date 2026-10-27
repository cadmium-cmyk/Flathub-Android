package com.example.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.MoreVert
import androidx.compose.material.icons.filled.Share
import androidx.compose.material.icons.filled.Terminal
import androidx.compose.material.icons.outlined.Bookmark
import androidx.compose.material.icons.outlined.BookmarkBorder
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.api.AppSummaryHit
import com.example.ui.theme.FlathubBlue
import com.example.ui.theme.InstalledBadgeBg
import com.example.ui.theme.InstalledBadgeText
import com.example.util.FlatpakHelper

@Composable
fun AppCard(
    app: AppSummaryHit,
    isBookmarked: Boolean = false,
    onAppClick: (String) -> Unit,
    onBookmarkToggle: (() -> Unit)? = null,
    modifier: Modifier = Modifier
) {
    val context = LocalContext.current
    var menuExpanded by remember { mutableStateOf(false) }

    Card(
        modifier = modifier
            .fillMaxWidth()
            .testTag("app_card_${app.cleanAppId}")
            .clip(RoundedCornerShape(16.dp))
            .border(
                width = 1.dp,
                color = MaterialTheme.colorScheme.outline.copy(alpha = 0.4f),
                shape = RoundedCornerShape(16.dp)
            )
            .clickable { onAppClick(app.cleanAppId) },
        shape = RoundedCornerShape(16.dp),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surface
        ),
        elevation = CardDefaults.cardElevation(defaultElevation = 0.5.dp)
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .padding(14.dp),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // App Icon
            Box(
                modifier = Modifier
                    .size(56.dp)
                    .clip(RoundedCornerShape(14.dp))
                    .background(MaterialTheme.colorScheme.surfaceVariant),
                contentAlignment = Alignment.Center
            ) {
                if (!app.icon.isNullOrBlank()) {
                    AsyncImage(
                        model = ImageRequest.Builder(context)
                            .data(app.icon)
                            .crossfade(true)
                            .build(),
                        contentDescription = app.cleanName,
                        contentScale = ContentScale.Fit,
                        modifier = Modifier
                            .size(52.dp)
                            .clip(RoundedCornerShape(12.dp))
                    )
                } else {
                    Text(
                        text = app.cleanName.take(1).uppercase(),
                        fontWeight = FontWeight.Bold,
                        fontSize = 22.sp,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
            }

            Spacer(modifier = Modifier.width(14.dp))

            // App Info (Title, verified check, summary, badge)
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(end = 4.dp),
                verticalArrangement = Arrangement.Center
            ) {
                // Name & Verified Badge
                Row(
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = app.cleanName,
                        fontWeight = FontWeight.SemiBold,
                        fontSize = 15.sp,
                        color = MaterialTheme.colorScheme.onSurface,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f, fill = false)
                    )

                    if (app.verificationVerified == true) {
                        Spacer(modifier = Modifier.width(4.dp))
                        Icon(
                            imageVector = Icons.Filled.CheckCircle,
                            contentDescription = "Verified Publisher",
                            tint = FlathubBlue,
                            modifier = Modifier.size(15.dp)
                        )
                    }
                }

                Spacer(modifier = Modifier.height(2.dp))

                // Summary
                Text(
                    text = if (app.cleanSummary.isNotBlank()) app.cleanSummary else "Flatpak application for Linux",
                    fontSize = 13.sp,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Optional Badge (Bookmarked / Installed status like in screenshot)
                if (isBookmarked) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Surface(
                        color = InstalledBadgeBg,
                        shape = RoundedCornerShape(12.dp)
                    ) {
                        Row(
                            modifier = Modifier.padding(horizontal = 8.dp, vertical = 2.dp),
                            verticalAlignment = Alignment.CenterVertically
                        ) {
                            Icon(
                                imageVector = Icons.Filled.CheckCircle,
                                contentDescription = null,
                                tint = InstalledBadgeText,
                                modifier = Modifier.size(12.dp)
                            )
                            Spacer(modifier = Modifier.width(4.dp))
                            Text(
                                text = "In Library",
                                fontSize = 11.sp,
                                fontWeight = FontWeight.Medium,
                                color = InstalledBadgeText
                            )
                        }
                    }
                } else if (!app.mainCategories.isNullOrEmpty()) {
                    Spacer(modifier = Modifier.height(6.dp))
                    Text(
                        text = app.mainCategories.first().replaceFirstChar { it.uppercase() },
                        fontSize = 11.sp,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.8f)
                    )
                }
            }

            // Quick Actions Dropdown
            Box {
                IconButton(
                    onClick = { menuExpanded = true },
                    modifier = Modifier.size(36.dp)
                ) {
                    Icon(
                        imageVector = Icons.Default.MoreVert,
                        contentDescription = "Options",
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                }

                DropdownMenu(
                    expanded = menuExpanded,
                    onDismissRequest = { menuExpanded = false }
                ) {
                    DropdownMenuItem(
                        text = { Text("View App") },
                        leadingIcon = {
                            Icon(Icons.AutoMirrored.Filled.OpenInNew, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            onAppClick(app.cleanAppId)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Download .flatpakref") },
                        leadingIcon = {
                            Icon(Icons.Default.Download, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            FlatpakHelper.downloadFlatpakref(context, app.cleanAppId, app.cleanName)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Copy Flathub Link") },
                        leadingIcon = {
                            Icon(Icons.Default.ContentCopy, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            FlatpakHelper.copyFlathubLink(context, app.cleanAppId)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Copy Install Command") },
                        leadingIcon = {
                            Icon(Icons.Default.Terminal, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            FlatpakHelper.copyInstallCommand(context, app.cleanAppId)
                        }
                    )
                    DropdownMenuItem(
                        text = { Text("Send to Device / Share") },
                        leadingIcon = {
                            Icon(Icons.Default.Share, contentDescription = null)
                        },
                        onClick = {
                            menuExpanded = false
                            FlatpakHelper.shareApp(context, app.cleanAppId, app.cleanName)
                        }
                    )
                    if (onBookmarkToggle != null) {
                        DropdownMenuItem(
                            text = { Text(if (isBookmarked) "Remove from Library" else "Save to Library") },
                            leadingIcon = {
                                Icon(
                                    imageVector = if (isBookmarked) Icons.Outlined.Bookmark else Icons.Outlined.BookmarkBorder,
                                    contentDescription = null
                                )
                            },
                            onClick = {
                                menuExpanded = false
                                onBookmarkToggle()
                            }
                        )
                    }
                }
            }
        }
    }
}
