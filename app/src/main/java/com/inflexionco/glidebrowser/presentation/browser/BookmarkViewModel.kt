package com.inflexionco.glidebrowser.presentation.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inflexionco.glidebrowser.data.local.entity.BookmarkEntity
import com.inflexionco.glidebrowser.domain.repository.BookmarkRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class BookmarkViewModel @Inject constructor(
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    fun getAllBookmarks(): Flow<List<BookmarkEntity>> {
        return bookmarkRepository.getAllBookmarks()
    }

    fun getBookmarksWithoutFolder(): Flow<List<BookmarkEntity>> {
        return bookmarkRepository.getBookmarksWithoutFolder()
    }

    fun getBookmarksByFolder(folderId: Long): Flow<List<BookmarkEntity>> {
        return bookmarkRepository.getBookmarksByFolder(folderId)
    }

    fun searchBookmarks(query: String): Flow<List<BookmarkEntity>> {
        return bookmarkRepository.searchAllBookmarks(query)
    }

    fun isBookmarked(url: String): Flow<Boolean> {
        return bookmarkRepository.isBookmarked(url)
    }

    fun toggleBookmark(title: String, url: String) {
        viewModelScope.launch {
            try {
                bookmarkRepository.toggleBookmark(title, url)
                Timber.d("Bookmark toggled for: $url")
            } catch (e: Exception) {
                Timber.e(e, "Error toggling bookmark")
            }
        }
    }

    fun addBookmark(title: String, url: String) {
        viewModelScope.launch {
            try {
                bookmarkRepository.addBookmark(title, url)
                Timber.d("Bookmark added: $url")
            } catch (e: Exception) {
                Timber.e(e, "Error adding bookmark")
            }
        }
    }

    fun updateBookmark(id: Long, title: String, url: String) {
        viewModelScope.launch {
            try {
                bookmarkRepository.updateBookmark(id, title, url)
                Timber.d("Bookmark updated: $url")
            } catch (e: Exception) {
                Timber.e(e, "Error updating bookmark")
            }
        }
    }

    fun removeBookmark(url: String) {
        viewModelScope.launch {
            try {
                bookmarkRepository.removeBookmark(url)
                Timber.d("Bookmark removed: $url")
            } catch (e: Exception) {
                Timber.e(e, "Error removing bookmark")
            }
        }
    }
}