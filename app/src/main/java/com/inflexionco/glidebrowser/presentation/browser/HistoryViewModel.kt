package com.inflexionco.glidebrowser.presentation.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inflexionco.glidebrowser.domain.model.HistoryItem
import com.inflexionco.glidebrowser.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HistoryViewModel @Inject constructor(
    private val historyRepository: HistoryRepository
) : ViewModel() {

    fun getRecentHistory(limit: Int = 50): Flow<List<HistoryItem>> {
        return historyRepository.getRecentHistory(limit)
    }

    fun searchHistory(query: String, limit: Int = 100): Flow<List<HistoryItem>> {
        return historyRepository.searchHistory(query, limit)
    }

    fun addHistory(title: String, url: String, faviconUrl: String? = null) {
        viewModelScope.launch {
            try {
                historyRepository.addHistory(title, url, faviconUrl)
                Timber.d("History added: $url")
            } catch (e: Exception) {
                Timber.e(e, "Error adding history")
            }
        }
    }

    fun clearAllHistory() {
        viewModelScope.launch {
            try {
                historyRepository.clearAllHistory()
                Timber.d("All history cleared")
            } catch (e: Exception) {
                Timber.e(e, "Error clearing history")
            }
        }
    }

    fun deleteHistoryItem(historyItem: HistoryItem) {
        viewModelScope.launch {
            try {
                historyRepository.deleteHistoryItem(historyItem)
                Timber.d("History item deleted: ${historyItem.url}")
            } catch (e: Exception) {
                Timber.e(e, "Error deleting history item")
            }
        }
    }
}