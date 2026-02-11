package com.inflexionco.glidebrowser.data.local.dao

import androidx.room.*
import com.inflexionco.glidebrowser.data.local.entity.HistoryEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface HistoryDao {

    @Query("SELECT * FROM history ORDER BY visitedAt DESC LIMIT :limit")
    fun getRecentHistory(limit: Int = 50): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE visitCount >= :minVisits ORDER BY visitCount DESC, visitedAt DESC LIMIT :limit")
    fun getMostVisited(limit: Int = 10, minVisits: Int = 2): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE title LIKE '%' || :query || '%' OR url LIKE '%' || :query || '%' ORDER BY visitCount DESC, visitedAt DESC LIMIT :limit")
    fun searchHistory(query: String, limit: Int = 5): Flow<List<HistoryEntity>>

    @Query("SELECT * FROM history WHERE id = :id")
    suspend fun getHistoryById(id: Long): HistoryEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertHistory(history: HistoryEntity): Long

    @Update
    suspend fun updateHistory(history: HistoryEntity)

    @Query("UPDATE history SET visitCount = visitCount + 1, visitedAt = :timestamp WHERE url = :url")
    suspend fun incrementVisitCount(url: String, timestamp: Long = System.currentTimeMillis())

    @Delete
    suspend fun deleteHistory(history: HistoryEntity)

    @Query("DELETE FROM history")
    suspend fun clearAllHistory()

    @Query("SELECT * FROM history WHERE url = :url LIMIT 1")
    suspend fun getHistoryByUrl(url: String): HistoryEntity?
}