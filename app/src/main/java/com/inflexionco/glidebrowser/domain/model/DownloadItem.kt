package com.inflexionco.glidebrowser.domain.model

import com.inflexionco.glidebrowser.data.local.entity.DownloadStatus

data class DownloadItem(
    val id: Long,
    val url: String,
    val fileName: String,
    val mimeType: String?,
    val filePath: String?,
    val fileSize: Long,
    val downloadedSize: Long,
    val status: DownloadStatus,
    val errorMessage: String?,
    val startedAt: Long,
    val completedAt: Long?
) {
    val progress: Float
        get() = if (fileSize > 0) {
            (downloadedSize.toFloat() / fileSize.toFloat())
        } else {
            0f
        }

    val progressPercentage: Int
        get() = (progress * 100).toInt()

    val isActive: Boolean
        get() = status == DownloadStatus.DOWNLOADING || status == DownloadStatus.PENDING

    val isComplete: Boolean
        get() = status == DownloadStatus.COMPLETED

    val canResume: Boolean
        get() = status == DownloadStatus.PAUSED || status == DownloadStatus.FAILED

    val canPause: Boolean
        get() = status == DownloadStatus.DOWNLOADING

    val canCancel: Boolean
        get() = status == DownloadStatus.DOWNLOADING ||
                status == DownloadStatus.PENDING ||
                status == DownloadStatus.PAUSED
}