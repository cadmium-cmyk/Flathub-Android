package com.example.data.local

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkedAppDao {

    @Query("SELECT * FROM bookmarked_apps ORDER BY savedAt DESC")
    fun getAllBookmarks(): Flow<List<BookmarkedAppEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertBookmark(app: BookmarkedAppEntity)

    @Query("DELETE FROM bookmarked_apps WHERE appId = :appId")
    suspend fun removeBookmark(appId: String)

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_apps WHERE appId = :appId)")
    fun isBookmarked(appId: String): Flow<Boolean>

    @Query("SELECT EXISTS(SELECT 1 FROM bookmarked_apps WHERE appId = :appId)")
    suspend fun isBookmarkedSync(appId: String): Boolean
}
