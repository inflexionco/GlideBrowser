package com.inflexionco.glidebrowser.data.repository

import com.inflexionco.glidebrowser.data.local.dao.DownloadDao
import com.inflexionco.glidebrowser.data.local.entity.DownloadEntity
import com.inflexionco.glidebrowser.data.local.entity.DownloadStatus
import com.inflexionco.glidebrowser.domain.model.DownloadItem
import com.inflexionco.glidebrowser.domain.repository.DownloadRepository
import com.inflexionco.glidebrowser.util.DownloadNotificationManager
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DownloadRepositoryImpl @Inject constructor(
    private val downloadDao: DownloadDao,
    private val notificationManager: DownloadNotificationManager
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

        // Update notification
        val download = downloadDao.getDownloadById(id)
        if (download != null) {
            val progress = if (download.fileSize > 0) {
                ((downloadedSize.toFloat() / download.fileSize) * 100).toInt()
            } else {
                0
            }
            notificationManager.updateDownloadNotification(
                downloadId = id,
                fileName = download.fileName,
                status = status,
                progress = progress,
                downloadedSize = downloadedSize,
                totalSize = download.fileSize
            )
        }
    }

    override suspend fun markDownloadCompleted(id: Long, filePath: String) {
        downloadDao.markDownloadCompleted(
            id = id,
            status = DownloadStatus.COMPLETED,
            completedAt = System.currentTimeMillis(),
            filePath = filePath
        )
        Timber.d("Download completed: $id -> $filePath")

        // Show completion notification
        val download = downloadDao.getDownloadById(id)
        if (download != null) {
            notificationManager.showDownloadCompleted(id, download.fileName)
        }
    }

    override suspend fun markDownloadFailed(id: Long, errorMessage: String) {
        downloadDao.markDownloadFailed(id, DownloadStatus.FAILED, errorMessage)
        Timber.e("Download failed: $id - $errorMessage")

        // Show failure notification
        val download = downloadDao.getDownloadById(id)
        if (download != null) {
            notificationManager.showDownloadFailed(id, download.fileName, errorMessage)
        }
    }

    override suspend fun pauseDownload(id: Long) {
        downloadDao.updateDownloadStatus(id, DownloadStatus.PAUSED)
        Timber.d("Download paused: $id")

        // Update notification to paused state
        val download = downloadDao.getDownloadById(id)
        if (download != null) {
            val progress = if (download.fileSize > 0) {
                ((download.downloadedSize.toFloat() / download.fileSize) * 100).toInt()
            } else {
                0
            }
            notificationManager.updateDownloadNotification(
                downloadId = id,
                fileName = download.fileName,
                status = DownloadStatus.PAUSED,
                progress = progress,
                downloadedSize = download.downloadedSize,
                totalSize = download.fileSize
            )
        }
    }

    override suspend fun resumeDownload(id: Long) {
        downloadDao.updateDownloadStatus(id, DownloadStatus.DOWNLOADING)
        Timber.d("Download resumed: $id")

        // Update notification to downloading state
        val download = downloadDao.getDownloadById(id)
        if (download != null) {
            val progress = if (download.fileSize > 0) {
                ((download.downloadedSize.toFloat() / download.fileSize) * 100).toInt()
            } else {
                0
            }
            notificationManager.updateDownloadNotification(
                downloadId = id,
                fileName = download.fileName,
                status = DownloadStatus.DOWNLOADING,
                progress = progress,
                downloadedSize = download.downloadedSize,
                totalSize = download.fileSize
            )
        }
    }

    override suspend fun cancelDownload(id: Long) {
        downloadDao.updateDownloadStatus(id, DownloadStatus.CANCELLED)
        Timber.d("Download cancelled: $id")

        // Cancel notification
        notificationManager.cancelNotification(id)
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