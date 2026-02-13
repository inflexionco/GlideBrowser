package com.inflexionco.glidebrowser.domain.repository

import com.inflexionco.glidebrowser.data.local.entity.DownloadStatus
import com.inflexionco.glidebrowser.domain.model.DownloadItem
import kotlinx.coroutines.flow.Flow

interface DownloadRepository {
    fun getAllDownloads(): Flow<List<DownloadItem>>
    fun getDownloadsByStatus(status: DownloadStatus): Flow<List<DownloadItem>>
    fun getActiveDownloads(): Flow<List<DownloadItem>>
    fun observeDownloadById(id: Long): Flow<DownloadItem?>
    suspend fun getDownloadById(id: Long): DownloadItem?
    suspend fun createDownload(url: String, fileName: String, mimeType: String?, fileSize: Long): Long
    suspend fun updateDownloadProgress(id: Long, downloadedSize: Long, status: DownloadStatus)
    suspend fun markDownloadCompleted(id: Long, filePath: String)
    suspend fun markDownloadFailed(id: Long, errorMessage: String)
    suspend fun pauseDownload(id: Long)
    suspend fun resumeDownload(id: Long)
    suspend fun cancelDownload(id: Long)
    suspend fun deleteDownload(id: Long)
    suspend fun clearCompletedDownloads()
    suspend fun clearAllDownloads()
}