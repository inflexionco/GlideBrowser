package com.inflexionco.glidebrowser.data.repository

import com.inflexionco.glidebrowser.data.local.dao.BookmarkDao
import com.inflexionco.glidebrowser.data.local.entity.BookmarkEntity
import com.inflexionco.glidebrowser.domain.repository.BookmarkRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class BookmarkRepositoryImpl @Inject constructor(
    private val bookmarkDao: BookmarkDao
) : BookmarkRepository {

    override fun getAllBookmarks(): Flow<List<BookmarkEntity>> {
        return bookmarkDao.getAllBookmarks()
    }

    override fun getBookmarksWithoutFolder(): Flow<List<BookmarkEntity>> {
        return bookmarkDao.getBookmarksWithoutFolder()
    }

    override fun getBookmarksByFolder(folderId: Long): Flow<List<BookmarkEntity>> {
        return bookmarkDao.getBookmarksByFolder(folderId)
    }

    override fun searchBookmarks(query: String, limit: Int): Flow<List<BookmarkEntity>> {
        return bookmarkDao.searchBookmarks(query, limit)
    }

    override fun searchAllBookmarks(query: String): Flow<List<BookmarkEntity>> {
        return bookmarkDao.searchAllBookmarks(query)
    }

    override fun isBookmarked(url: String): Flow<Boolean> {
        return bookmarkDao.isBookmarked(url)
    }

    override suspend fun getBookmarkByUrl(url: String): BookmarkEntity? {
        return bookmarkDao.getBookmarkByUrl(url)
    }

    override suspend fun addBookmark(title: String, url: String, folderId: Long?) {
        val bookmark = BookmarkEntity(
            title = title,
            url = url,
            folderId = folderId
        )
        bookmarkDao.insertBookmark(bookmark)
    }

    override suspend fun updateBookmark(id: Long, title: String, url: String, folderId: Long?) {
        val bookmark = bookmarkDao.getBookmarkById(id)
        if (bookmark != null) {
            val updated = bookmark.copy(title = title, url = url, folderId = folderId)
            bookmarkDao.updateBookmark(updated)
        }
    }

    override suspend fun moveBookmarkToFolder(bookmarkId: Long, folderId: Long?) {
        val bookmark = bookmarkDao.getBookmarkById(bookmarkId)
        if (bookmark != null) {
            val updated = bookmark.copy(folderId = folderId)
            bookmarkDao.updateBookmark(updated)
        }
    }

    override suspend fun removeBookmark(url: String) {
        bookmarkDao.deleteBookmarkByUrl(url)
    }

    override suspend fun toggleBookmark(title: String, url: String) {
        val existing = bookmarkDao.getBookmarkByUrl(url)
        if (existing != null) {
            bookmarkDao.deleteBookmarkByUrl(url)
        } else {
            addBookmark(title, url)
        }
    }
}