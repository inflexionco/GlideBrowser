package com.inflexionco.glidebrowser.data.local.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "bookmark_folders")
data class BookmarkFolderEntity(
    @PrimaryKey(autoGenerate = true)
    val id: Long = 0,
    val name: String,
    val parentFolderId: Long? = null, // null for root-level folders
    val position: Int = 0, // For custom ordering
    val createdAt: Long = System.currentTimeMillis()
)