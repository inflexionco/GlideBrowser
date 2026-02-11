package com.inflexionco.glidebrowser.presentation.browser

import androidx.lifecycle.ViewModel
import com.inflexionco.glidebrowser.domain.repository.BookmarkRepository
import com.inflexionco.glidebrowser.domain.repository.HistoryRepository
import com.inflexionco.glidebrowser.presentation.browser.components.SuggestionSource
import com.inflexionco.glidebrowser.presentation.browser.components.UrlSuggestion
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.combine
import javax.inject.Inject

@HiltViewModel
class SuggestionViewModel @Inject constructor(
    private val historyRepository: HistoryRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    fun getSuggestions(query: String): Flow<List<UrlSuggestion>> {
        if (query.isBlank()) {
            return kotlinx.coroutines.flow.flowOf(emptyList())
        }

        val historyFlow = historyRepository.searchHistory(query, limit = 3)
        val bookmarkFlow = bookmarkRepository.searchBookmarks(query, limit = 3)

        return combine(historyFlow, bookmarkFlow) { historyItems, bookmarks ->
            val suggestions = mutableListOf<UrlSuggestion>()

            // Add bookmarks first (they're more intentionally saved)
            suggestions.addAll(bookmarks.map { bookmark ->
                UrlSuggestion(
                    title = bookmark.title,
                    url = bookmark.url,
                    source = SuggestionSource.BOOKMARK
                )
            })

            // Add history items
            suggestions.addAll(historyItems.map { historyItem ->
                UrlSuggestion(
                    title = historyItem.title,
                    url = historyItem.url,
                    source = SuggestionSource.HISTORY
                )
            })

            // Remove duplicates (prefer bookmark over history)
            suggestions.distinctBy { it.url }.take(5)
        }
    }
}