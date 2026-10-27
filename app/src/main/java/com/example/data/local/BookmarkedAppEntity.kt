package com.example.data.local

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmarked_apps")
data class BookmarkedAppEntity(
    @PrimaryKey val appId: String,
    val name: String,
    val summary: String,
    val icon: String?,
    val category: String?,
    val verified: Boolean = false,
    val savedAt: Long = System.currentTimeMillis()
)
