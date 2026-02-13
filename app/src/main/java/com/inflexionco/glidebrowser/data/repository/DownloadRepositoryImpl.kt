package com.inflexionco.glidebrowser.data.repository

import com.inflexionco.glidebrowser.data.local.dao.DownloadDao
import com.inflexionco.glidebrowser.data.local.entity.DownloadEntity
import com.inflexionco.glidebrowser.data.local.entity.DownloadStatus
import com.inflexionco.glidebrowser.domain.model.DownloadItem
import com.inflexionco.glidebrowser.domain.repository.DownloadRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    private val downloadDao: DownloadDao
) : DownloadRepository {

    override fun getAllDownloads(): Flow<List<DownloadItem>> {
        return downloadDao.getAllDownloads().map { entities ->
            entities.map { it.toDownloadItem() }
        }
    }

    override fun getDownloadsByStatus(status: DownloadStatus): Flow<List<DownloadItem>> {
        return downloadDao.getDownloadsByStatus(status).map { entities ->
            entities.map { it.toDownloadItem() }
        }
    }

    override fun getActiveDownloads(): Flow<List<DownloadItem>> {
        val activeStatuses = listOf(DownloadStatus.DOWNLOADING, DownloadStatus.PENDING)
        return downloadDao.getDownloadsByStatuses(activeStatuses).map { entities ->
            entities.map { it.toDownloadItem() }
        }
    }

    override fun observeDownloadById(id: Long): Flow<DownloadItem?> {
        return downloadDao.observeDownloadById(id).map { entity ->
            entity?.toDownloadItem()
        }
    }

    override suspend fun getDownloadById(id: Long): DownloadItem? {
        return downloadDao.getDownloadById(id)?.toDownloadItem()
    }

    override suspend fun createDownload(
        url: String,
        fileName: String,
        mimeType: String?,
        fileSize: Long
    ): Long {
        val download = DownloadEntity(
            url = url,
            fileName = fileName,
            mimeType = mimeType,
            filePath = null,
            fileSize = fileSize,
            status = DownloadStatus.PENDING
        )
        val id = downloadDao.insertDownload(download)
        Timber.d("Download created: $fileName (ID: $id)")
        return id
    }

    override suspend fun updateDownloadProgress(id: Long, downloadedSize: Long, status: DownloadStatus) {
        downloadDao.updateDownloadProgress(id, downloadedSize, status)
    }

    override suspend fun markDownloadCompleted(id: Long, filePath: String) {
        downloadDao.markDownloadCompleted(
            id = id,
            status = DownloadStatus.COMPLETED,
            completedAt = System.currentTimeMillis(),
            filePath = filePath
        )
        Timber.d("Download completed: $id -> $filePath")
    }

    override suspend fun markDownloadFailed(id: Long, errorMessage: String) {
        downloadDao.markDownloadFailed(id, DownloadStatus.FAILED, errorMessage)
        Timber.e("Download failed: $id - $errorMessage")
    }

    override suspend fun pauseDownload(id: Long) {
        downloadDao.updateDownloadStatus(id, DownloadStatus.PAUSED)
        Timber.d("Download paused: $id")
    }

    override suspend fun resumeDownload(id: Long) {
        downloadDao.updateDownloadStatus(id, DownloadStatus.DOWNLOADING)
        Timber.d("Download resumed: $id")
    }

    override suspend fun cancelDownload(id: Long) {
        downloadDao.updateDownloadStatus(id, DownloadStatus.CANCELLED)
        Timber.d("Download cancelled: $id")
    }

    override suspend fun deleteDownload(id: Long) {
        downloadDao.deleteDownloadById(id)
        Timber.d("Download deleted: $id")
    }

    override suspend fun clearCompletedDownloads() {
        downloadDao.deleteDownloadsByStatus(DownloadStatus.COMPLETED)
        Timber.d("Cleared completed downloads")
    }

    override suspend fun clearAllDownloads() {
        downloadDao.clearAllDownloads()
        Timber.d("Cleared all downloads")
    }

    private fun DownloadEntity.toDownloadItem() = DownloadItem(
        id = id,
        url = url,
        fileName = fileName,
        mimeType = mimeType,
        filePath = filePath,
        fileSize = fileSize,
        downloadedSize = downloadedSize,
        status = status,
        errorMessage = errorMessage,
        startedAt = startedAt,
        completedAt = completedAt
    )
}