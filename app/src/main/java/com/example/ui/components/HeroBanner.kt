package com.example.ui.components

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.ArrowForward
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowLeft
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material.icons.automirrored.filled.OpenInNew
import androidx.compose.material.icons.filled.ContentCopy
import androidx.compose.material.icons.filled.Download
import androidx.compose.material.icons.filled.Visibility
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.testTag
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.data.api.AppstreamDetail
import com.example.ui.theme.HeroBannerDefaultBg
import com.example.util.FlatpakHelper

@Composable
fun HeroBanner(
    featuredApps: List<AppstreamDetail>,
    onAppClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (featuredApps.isEmpty()) return

    val context = LocalContext.current
    var currentIndex by remember { mutableIntStateOf(0) }
    val currentApp = featuredApps.getOrNull(currentIndex) ?: featuredApps.first()

    // Determine banner background color based on app branding
    val bgColor = remember(currentApp.primaryBrandingColor) {
        currentApp.primaryBrandingColor?.let { hex ->
            try {
                Color(android.graphics.Color.parseColor(hex))
            } catch (e: Exception) {
                HeroBannerDefaultBg
            }
        } ?: HeroBannerDefaultBg
    }

    // High contrast content text color
    val isLightBg = remember(bgColor) {
        val luminance = (0.299 * bgColor.red + 0.587 * bgColor.green + 0.114 * bgColor.blue)
        luminance > 0.5
    }
    val contentColor = if (isLightBg) Color(0xFF1E2538) else Color.White
    val contentSecondaryColor = if (isLightBg) Color(0xFF2E384D) else Color(0xFFE2E8F0)

    BoxWithConstraints(
        modifier = modifier
            .fillMaxWidth()
            .testTag("hero_banner")
    ) {
        val isTablet = maxWidth >= 680.dp

        Card(
            shape = RoundedCornerShape(24.dp),
            colors = CardDefaults.cardColors(containerColor = bgColor),
            elevation = CardDefaults.cardElevation(defaultElevation = 2.dp),
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(24.dp))
                .clickable {
                    currentApp.id?.let { onAppClick(it) }
                }
        ) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(if (isTablet) 20.dp else 16.dp)
            ) {
                AnimatedContent(
                    targetState = currentApp,
                    transitionSpec = { fadeIn() togetherWith fadeOut() },
                    label = "hero_banner_transition"
                ) { app ->
                    if (isTablet) {
                        // Tablet / Wide Layout: Left App Info + Right Mockup Preview
                        Row(
                            modifier = Modifier
                                .fillMaxWidth()
                                .heightIn(min = 280.dp),
                            verticalAlignment = Alignment.CenterVertically,
                            horizontalArrangement = Arrangement.SpaceBetween
                        ) {
                            // Left Column: Icon, Title, Summary, Actions
                            Column(
                                modifier = Modifier
                                    .weight(0.42f)
                                    .padding(start = 36.dp, end = 16.dp),
                                horizontalAlignment = Alignment.CenterHorizontally,
                                verticalArrangement = Arrangement.Center
                            ) {
                                // App Icon
                                Box(
                                    modifier = Modifier
                                        .size(86.dp)
                                        .shadow(8.dp, RoundedCornerShape(20.dp))
                                        .clip(RoundedCornerShape(20.dp))
                                        .background(Color.White.copy(alpha = 0.95f)),
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
                                            modifier = Modifier.size(76.dp)
                                        )
                                    } else {
                                        Text(
                                            text = (app.name ?: "App").take(1),
                                            fontSize = 32.sp,
                                            fontWeight = FontWeight.Bold,
                                            color = Color(0xFF1E2538)
                                        )
                                    }
                                }

                                Spacer(modifier = Modifier.height(14.dp))

                                Text(
                                    text = app.name ?: "Featured App",
                                    fontSize = 26.sp,
                                    fontWeight = FontWeight.ExtraBold,
                                    color = contentColor,
                                    textAlign = TextAlign.Center,
                                    maxLines = 1,
                                    overflow = TextOverflow.Ellipsis
                                )

                                Spacer(modifier = Modifier.height(4.dp))

                                Text(
                                    text = app.summary ?: "Discover this week's featured Flatpak application",
                                    fontSize = 14.sp,
                                    color = contentSecondaryColor,
                                    textAlign = TextAlign.Center,
                                    maxLines = 2,
                                    overflow = TextOverflow.Ellipsis
                                )
                            }

                            // Right Column: Window Frame with Screenshot
                            val screenshotUrl = app.bestScreenshots.firstOrNull()
                            Box(
                                modifier = Modifier
                                    .weight(0.58f)
                                    .padding(end = 36.dp, top = 8.dp, bottom = 8.dp)
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White.copy(alpha = 0.95f))
                                    .shadow(12.dp, RoundedCornerShape(16.dp))
                            ) {
                                Column {
                                    // Window title bar with dots
                                    Row(
                                        modifier = Modifier
                                            .fillMaxWidth()
                                            .background(Color(0xFFE5E7EB))
                                            .padding(horizontal = 12.dp, vertical = 6.dp),
                                        verticalAlignment = Alignment.CenterVertically
                                    ) {
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFEF4444)))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFFF59E0B)))
                                        Spacer(modifier = Modifier.width(6.dp))
                                        Box(modifier = Modifier.size(8.dp).clip(CircleShape).background(Color(0xFF10B981)))
                                        Spacer(modifier = Modifier.width(12.dp))
                                        Text(
                                            text = app.name ?: "Preview",
                                            fontSize = 11.sp,
                                            color = Color(0xFF4B5563),
                                            fontWeight = FontWeight.Medium
                                        )
                                    }

                                    // Screenshot or placeholder
                                    if (!screenshotUrl.isNullOrBlank()) {
                                        AsyncImage(
                                            model = ImageRequest.Builder(context)
                                                .data(screenshotUrl)
                                                .crossfade(true)
                                                .build(),
                                            contentDescription = "Screenshot of ${app.name}",
                                            contentScale = ContentScale.FillWidth,
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(200.dp)
                                        )
                                    } else {
                                        Box(
                                            modifier = Modifier
                                                .fillMaxWidth()
                                                .height(180.dp)
                                                .background(Color(0xFFF3F4F6)),
                                            contentAlignment = Alignment.Center
                                        ) {
                                            Text("Screenshot Preview", color = Color.Gray, fontSize = 13.sp)
                                        }
                                    }
                                }
                            }
                        }
                    } else {
                        // Phone Layout: Compact vertical stack
                        Column(
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(vertical = 12.dp, horizontal = 36.dp),
                            horizontalAlignment = Alignment.CenterHorizontally
                        ) {
                            // App Icon
                            Box(
                                modifier = Modifier
                                    .size(68.dp)
                                    .shadow(6.dp, RoundedCornerShape(16.dp))
                                    .clip(RoundedCornerShape(16.dp))
                                    .background(Color.White),
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
                                        modifier = Modifier.size(58.dp)
                                    )
                                } else {
                                    Text(
                                        text = (app.name ?: "A").take(1),
                                        fontSize = 28.sp,
                                        fontWeight = FontWeight.Bold,
                                        color = Color(0xFF1E2538)
                                    )
                                }
                            }

                            Spacer(modifier = Modifier.height(8.dp))

                            Text(
                                text = app.name ?: "Featured App",
                                fontSize = 20.sp,
                                fontWeight = FontWeight.Bold,
                                color = contentColor,
                                textAlign = TextAlign.Center
                            )

                            Spacer(modifier = Modifier.height(2.dp))

                            Text(
                                text = app.summary ?: "Featured on Flathub",
                                fontSize = 13.sp,
                                color = contentSecondaryColor,
                                textAlign = TextAlign.Center,
                                maxLines = 2,
                                overflow = TextOverflow.Ellipsis
                            )

                            Spacer(modifier = Modifier.height(6.dp))

                            val previewScreenshot = app.bestScreenshots.firstOrNull()
                            if (!previewScreenshot.isNullOrBlank()) {
                                Spacer(modifier = Modifier.height(12.dp))
                                Card(
                                    shape = RoundedCornerShape(12.dp),
                                    modifier = Modifier
                                        .fillMaxWidth()
                                        .height(140.dp)
                                        .clip(RoundedCornerShape(12.dp)),
                                    colors = CardDefaults.cardColors(containerColor = Color.White.copy(alpha = 0.9f))
                                ) {
                                    AsyncImage(
                                        model = ImageRequest.Builder(context)
                                            .data(previewScreenshot)
                                            .crossfade(true)
                                            .build(),
                                        contentDescription = "Preview of ${app.name}",
                                        contentScale = ContentScale.Crop,
                                        modifier = Modifier.fillMaxSize()
                                    )
                                }
                            }
                        }
                    }
                }

                // Left Arrow
                if (featuredApps.size > 1) {
                    IconButton(
                        onClick = {
                            currentIndex = if (currentIndex == 0) featuredApps.lastIndex else currentIndex - 1
                        },
                        modifier = Modifier
                            .align(Alignment.CenterStart)
                            .padding(start = 2.dp)
                            .size(36.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowLeft,
                            contentDescription = "Previous Featured App"
                        )
                    }

                    // Right Arrow
                    IconButton(
                        onClick = {
                            currentIndex = (currentIndex + 1) % featuredApps.size
                        },
                        modifier = Modifier
                            .align(Alignment.CenterEnd)
                            .padding(end = 2.dp)
                            .size(36.dp),
                        colors = IconButtonDefaults.iconButtonColors(
                            containerColor = Color.Black.copy(alpha = 0.2f),
                            contentColor = Color.White
                        )
                    ) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.KeyboardArrowRight,
                            contentDescription = "Next Featured App"
                        )
                    }
                }
            }
        }
    }
}
