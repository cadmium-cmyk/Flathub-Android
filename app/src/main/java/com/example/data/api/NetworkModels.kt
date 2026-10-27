package com.example.data.api

import com.squareup.moshi.Json
import com.squareup.moshi.JsonClass

@JsonClass(generateAdapter = true)
data class CollectionResponse(
    val hits: List<AppSummaryHit> = emptyList(),
    @Json(name = "total_hits") val totalHits: Int? = null
)

@JsonClass(generateAdapter = true)
data class AppSummaryHit(
    @Json(name = "app_id") val appId: String? = null,
    val id: String? = null,
    val name: String? = null,
    val summary: String? = null,
    val description: String? = null,
    val icon: String? = null,
    @Json(name = "project_license") val projectLicense: String? = null,
    @Json(name = "is_free_license") val isFreeLicense: Boolean? = null,
    @Json(name = "main_categories") val rawMainCategories: Any? = null,
    @Json(name = "developer_name") val developerName: String? = null,
    @Json(name = "verification_verified") val verificationVerified: Boolean? = null
) {
    val cleanAppId: String
        get() = appId ?: id.orEmpty().replace('_', '.')

    val cleanName: String
        get() = name ?: cleanAppId

    val cleanSummary: String
        get() = summary.orEmpty().trim()

    val mainCategories: List<String>
        get() = when (val raw = rawMainCategories) {
            is String -> if (raw.isBlank()) emptyList() else listOf(raw)
            is List<*> -> raw.mapNotNull { it as? String }
            else -> emptyList()
        }
}

@JsonClass(generateAdapter = true)
data class AppPickWeeklyResponse(
    val apps: List<AppPickItem> = emptyList()
)

@JsonClass(generateAdapter = true)
data class AppPickItem(
    @Json(name = "app_id") val appId: String = "",
    val position: Int? = null,
    @Json(name = "isFullscreen") val isFullscreen: Boolean? = null
)

@JsonClass(generateAdapter = true)
data class SearchRequest(
    val query: String,
    val page: Int = 1,
    @Json(name = "hits_per_page") val hitsPerPage: Int = 24
)

@JsonClass(generateAdapter = true)
data class SearchResponse(
    val hits: List<AppSummaryHit> = emptyList(),
    val query: String? = null,
    val page: Int? = null,
    val totalHits: Int? = null
)

@JsonClass(generateAdapter = true)
data class AppstreamDetail(
    val id: String? = null,
    val name: String? = null,
    val summary: String? = null,
    val description: String? = null,
    @Json(name = "developer_name") val developerName: String? = null,
    @Json(name = "project_license") val projectLicense: String? = null,
    @Json(name = "is_free_license") val isFreeLicense: Boolean? = null,
    val icon: String? = null,
    val categories: List<String>? = null,
    val screenshots: List<ScreenshotItem>? = null,
    val releases: List<ReleaseItem>? = null,
    val urls: Map<String, String?>? = null,
    val branding: List<BrandingItem>? = null
) {
    val bestScreenshots: List<String>
        get() {
            return screenshots.orEmpty().mapNotNull { item ->
                val sizes = item.sizes.orEmpty()
                sizes.firstOrNull { it.width?.toIntOrNull() in 700..1300 }?.src
                    ?: sizes.lastOrNull()?.src
                    ?: sizes.firstOrNull()?.src
            }
        }

    val primaryBrandingColor: String?
        get() {
            return branding?.firstOrNull { it.type == "primary" && !it.value.isNullOrBlank() }?.value
        }
}

@JsonClass(generateAdapter = true)
data class ScreenshotItem(
    val caption: String? = null,
    val default: Boolean? = null,
    val sizes: List<ScreenshotSize> = emptyList()
)

@JsonClass(generateAdapter = true)
data class ScreenshotSize(
    val src: String = "",
    val width: String? = null,
    val height: String? = null,
    val scale: String? = null
)

@JsonClass(generateAdapter = true)
data class ReleaseItem(
    val version: String? = null,
    val timestamp: String? = null,
    val description: String? = null,
    val type: String? = null
)

@JsonClass(generateAdapter = true)
data class BrandingItem(
    val type: String? = null,
    val value: String? = null,
    @Json(name = "scheme_preference") val schemePreference: String? = null
)
