package com.inflexionco.glidebrowser.domain.repository

import com.inflexionco.glidebrowser.domain.model.HistoryItem
import kotlinx.coroutines.flow.Flow

interface HistoryRepository {
    fun getRecentHistory(limit: Int = 50): Flow<List<HistoryItem>>
    fun getMostVisited(limit: Int = 10, minVisits: Int = 2): Flow<List<HistoryItem>>
    fun searchHistory(query: String, limit: Int = 5): Flow<List<HistoryItem>>
    suspend fun addHistory(title: String, url: String, faviconUrl: String? = null)
    suspend fun clearAllHistory()
    suspend fun deleteHistoryItem(historyItem: HistoryItem)
}