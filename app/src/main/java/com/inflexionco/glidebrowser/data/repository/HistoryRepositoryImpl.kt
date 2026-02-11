package com.inflexionco.glidebrowser.data.repository

import com.inflexionco.glidebrowser.data.local.dao.HistoryDao
import com.inflexionco.glidebrowser.data.local.entity.HistoryEntity
import com.inflexionco.glidebrowser.domain.model.HistoryItem
import com.inflexionco.glidebrowser.domain.repository.HistoryRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class HistoryRepositoryImpl @Inject constructor(
    private val historyDao: HistoryDao
) : HistoryRepository {

    override fun getRecentHistory(limit: Int): Flow<List<HistoryItem>> {
        return historyDao.getRecentHistory(limit).map { entities ->
            entities.map { it.toHistoryItem() }
        }
    }

    override fun searchHistory(query: String, limit: Int): Flow<List<HistoryItem>> {
        return historyDao.searchHistory(query, limit).map { entities ->
            entities.map { it.toHistoryItem() }
        }
    }

    override suspend fun addHistory(title: String, url: String, faviconUrl: String?) {
        try {
            // Check if this URL already exists in history
            val existing = historyDao.getHistoryByUrl(url)

            if (existing != null) {
                // URL already exists, increment visit count
                historyDao.incrementVisitCount(url, System.currentTimeMillis())
                Timber.d("Updated history: $url (visit count incremented)")
            } else {
                // New history entry
                val historyEntity = HistoryEntity(
                    title = title,
                    url = url,
                    faviconUrl = faviconUrl,
                    visitedAt = System.currentTimeMillis(),
                    visitCount = 1
                )
                historyDao.insertHistory(historyEntity)
                Timber.d("Added new history: $url")
            }
        } catch (e: Exception) {
            Timber.e(e, "Error adding history")
        }
    }

    override suspend fun clearAllHistory() {
        try {
            historyDao.clearAllHistory()
            Timber.d("Cleared all history")
        } catch (e: Exception) {
            Timber.e(e, "Error clearing history")
        }
    }

    override suspend fun deleteHistoryItem(historyItem: HistoryItem) {
        try {
            val entity = HistoryEntity(
                id = historyItem.id,
                title = historyItem.title,
                url = historyItem.url,
                faviconUrl = historyItem.faviconUrl,
                visitedAt = historyItem.visitedAt,
                visitCount = historyItem.visitCount
            )
            historyDao.deleteHistory(entity)
            Timber.d("Deleted history item: ${historyItem.url}")
        } catch (e: Exception) {
            Timber.e(e, "Error deleting history item")
        }
    }

    private fun HistoryEntity.toHistoryItem() = HistoryItem(
        id = id,
        title = title,
        url = url,
        faviconUrl = faviconUrl,
        visitedAt = visitedAt,
        visitCount = visitCount
    )
}