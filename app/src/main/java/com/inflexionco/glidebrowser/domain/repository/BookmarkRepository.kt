package com.inflexionco.glidebrowser.domain.repository

import com.inflexionco.glidebrowser.data.local.entity.BookmarkEntity
import kotlinx.coroutines.flow.Flow

interface BookmarkRepository {
    fun getAllBookmarks(): Flow<List<BookmarkEntity>>
    fun searchBookmarks(query: String, limit: Int = 5): Flow<List<BookmarkEntity>>
    fun isBookmarked(url: String): Flow<Boolean>
    suspend fun getBookmarkByUrl(url: String): BookmarkEntity?
    suspend fun addBookmark(title: String, url: String)
    suspend fun updateBookmark(id: Long, title: String, url: String)
    suspend fun removeBookmark(url: String)
    suspend fun toggleBookmark(title: String, url: String)
}