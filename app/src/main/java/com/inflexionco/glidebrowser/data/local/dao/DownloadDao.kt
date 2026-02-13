package com.inflexionco.glidebrowser.data.local.dao

import androidx.room.*
import com.inflexionco.glidebrowser.data.local.entity.DownloadEntity
import com.inflexionco.glidebrowser.data.local.entity.DownloadStatus
import kotlinx.coroutines.flow.Flow

@Dao
interface DownloadDao {

    @Query("SELECT * FROM downloads ORDER BY startedAt DESC")
    fun getAllDownloads(): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status = :status ORDER BY startedAt DESC")
    fun getDownloadsByStatus(status: DownloadStatus): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE status IN (:statuses) ORDER BY startedAt DESC")
    fun getDownloadsByStatuses(statuses: List<DownloadStatus>): Flow<List<DownloadEntity>>

    @Query("SELECT * FROM downloads WHERE id = :id")
    suspend fun getDownloadById(id: Long): DownloadEntity?

    @Query("SELECT * FROM downloads WHERE id = :id")
    fun observeDownloadById(id: Long): Flow<DownloadEntity?>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertDownload(download: DownloadEntity): Long

    @Update
    suspend fun updateDownload(download: DownloadEntity)

    @Delete
    suspend fun deleteDownload(download: DownloadEntity)

    @Query("DELETE FROM downloads WHERE id = :id")
    suspend fun deleteDownloadById(id: Long)

    @Query("DELETE FROM downloads WHERE status = :status")
    suspend fun deleteDownloadsByStatus(status: DownloadStatus)

    @Query("DELETE FROM downloads")
    suspend fun clearAllDownloads()

    @Query("UPDATE downloads SET status = :status WHERE id = :id")
    suspend fun updateDownloadStatus(id: Long, status: DownloadStatus)

    @Query("UPDATE downloads SET downloadedSize = :size, status = :status WHERE id = :id")
    suspend fun updateDownloadProgress(id: Long, size: Long, status: DownloadStatus)

    @Query("UPDATE downloads SET status = :status, completedAt = :completedAt, filePath = :filePath WHERE id = :id")
    suspend fun markDownloadCompleted(id: Long, status: DownloadStatus, completedAt: Long, filePath: String)

    @Query("UPDATE downloads SET status = :status, errorMessage = :errorMessage WHERE id = :id")
    suspend fun markDownloadFailed(id: Long, status: DownloadStatus, errorMessage: String)

    @Query("SELECT COUNT(*) FROM downloads WHERE status = :status")
    suspend fun getDownloadCountByStatus(status: DownloadStatus): Int
}