package com.example.data

import com.example.data.api.AppPickWeeklyResponse
import com.example.data.api.AppSummaryHit
import com.example.data.api.AppstreamDetail
import com.example.data.api.FlathubApiService
import com.example.data.api.SearchRequest
import com.example.data.local.BookmarkedAppDao
import com.example.data.local.BookmarkedAppEntity
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.withContext
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

class FlathubRepository(
    private val api: FlathubApiService,
    private val dao: BookmarkedAppDao
) {
    // In-memory cache for responsive browsing
    private val trendingCache = mutableListOf<AppSummaryHit>()
    private val popularCache = mutableListOf<AppSummaryHit>()
    private val recentlyAddedCache = mutableListOf<AppSummaryHit>()
    private val recentlyUpdatedCache = mutableListOf<AppSummaryHit>()
    private val categoryCache = mutableMapOf<String, List<AppSummaryHit>>()

    suspend fun getTrending(forceRefresh: Boolean = false): List<AppSummaryHit> = withContext(Dispatchers.IO) {
        if (!forceRefresh && trendingCache.isNotEmpty()) return@withContext trendingCache
        val response = api.getTrending(page = 1, perPage = 24)
        trendingCache.clear()
        trendingCache.addAll(response.hits)
        response.hits
    }

    suspend fun getPopular(forceRefresh: Boolean = false): List<AppSummaryHit> = withContext(Dispatchers.IO) {
        if (!forceRefresh && popularCache.isNotEmpty()) return@withContext popularCache
        val response = api.getPopular(page = 1, perPage = 24)
        popularCache.clear()
        popularCache.addAll(response.hits)
        response.hits
    }

    suspend fun getRecentlyAdded(forceRefresh: Boolean = false): List<AppSummaryHit> = withContext(Dispatchers.IO) {
        if (!forceRefresh && recentlyAddedCache.isNotEmpty()) return@withContext recentlyAddedCache
        val response = api.getRecentlyAdded(page = 1, perPage = 24)
        recentlyAddedCache.clear()
        recentlyAddedCache.addAll(response.hits)
        response.hits
    }

    suspend fun getRecentlyUpdated(forceRefresh: Boolean = false): List<AppSummaryHit> = withContext(Dispatchers.IO) {
        if (!forceRefresh && recentlyUpdatedCache.isNotEmpty()) return@withContext recentlyUpdatedCache
        val response = api.getRecentlyUpdated(page = 1, perPage = 24)
        recentlyUpdatedCache.clear()
        recentlyUpdatedCache.addAll(response.hits)
        response.hits
    }

    suspend fun getCategoryApps(category: String, forceRefresh: Boolean = false): List<AppSummaryHit> = withContext(Dispatchers.IO) {
        if (!forceRefresh && categoryCache.containsKey(category)) return@withContext categoryCache[category].orEmpty()
        val response = api.getCategoryApps(category = category, page = 1, perPage = 12)
        categoryCache[category] = response.hits
        response.hits
    }

    suspend fun getFeaturedHeroApps(): List<AppstreamDetail> = withContext(Dispatchers.IO) {
        val todayStr = SimpleDateFormat("yyyy-MM-dd", Locale.US).format(Date())
        val featuredList = mutableListOf<AppstreamDetail>()

        try {
            val weeklyPicks = api.getAppsOfTheWeek(todayStr)
            for (pick in weeklyPicks.apps.take(4)) {
                try {
                    val detail = api.getAppstream(pick.appId)
                    featuredList.add(detail)
                } catch (e: Exception) {
                    // Ignore single failure
                }
            }
        } catch (e: Exception) {
            // Fallback: fetch Tangram and other featured apps
        }

        if (featuredList.isEmpty()) {
            val fallbackIds = listOf("re.sonny.Tangram", "org.mozilla.firefox", "org.videolan.VLC", "org.gimp.GIMP")
            for (id in fallbackIds) {
                try {
                    val detail = api.getAppstream(id)
                    featuredList.add(detail)
                } catch (e: Exception) {
                    // Ignore
                }
            }
        }
        featuredList
    }

    suspend fun getAppDetails(appId: String): AppstreamDetail = withContext(Dispatchers.IO) {
        api.getAppstream(appId)
    }

    suspend fun search(query: String): List<AppSummaryHit> = withContext(Dispatchers.IO) {
        if (query.isBlank()) return@withContext emptyList()
        val response = api.searchApps(SearchRequest(query = query, hitsPerPage = 30))
        response.hits
    }

    // Bookmarks / Library
    fun getBookmarkedApps(): Flow<List<BookmarkedAppEntity>> {
        return dao.getAllBookmarks()
    }

    fun isBookmarked(appId: String): Flow<Boolean> {
        return dao.isBookmarked(appId)
    }

    suspend fun toggleBookmark(appId: String, name: String, summary: String, icon: String?, category: String?, verified: Boolean) = withContext(Dispatchers.IO) {
        if (dao.isBookmarkedSync(appId)) {
            dao.removeBookmark(appId)
        } else {
            dao.insertBookmark(
                BookmarkedAppEntity(
                    appId = appId,
                    name = name,
                    summary = summary,
                    icon = icon,
                    category = category,
                    verified = verified
                )
            )
        }
    }
}
