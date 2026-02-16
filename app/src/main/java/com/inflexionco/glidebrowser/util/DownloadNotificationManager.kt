package com.inflexionco.glidebrowser.util

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.inflexionco.glidebrowser.MainActivity
import com.inflexionco.glidebrowser.R
import com.inflexionco.glidebrowser.data.local.entity.DownloadStatus
import dagger.hilt.android.qualifiers.ApplicationContext
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages download notifications for Android TV
 * Shows progress, completion, and failure notifications
 */
@Singleton
class DownloadNotificationManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private val notificationManager = NotificationManagerCompat.from(context)

    companion object {
        private const val CHANNEL_ID = "download_channel"
        private const val CHANNEL_NAME = "Downloads"
        private const val CHANNEL_DESCRIPTION = "Download progress and completion notifications"
        private const val NOTIFICATION_ID_BASE = 1000
    }

    init {
        createNotificationChannel()
    }

    private fun createNotificationChannel() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val importance = NotificationManager.IMPORTANCE_LOW
            val channel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, importance).apply {
                description = CHANNEL_DESCRIPTION
            }

            val notificationManager = context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(channel)
            Timber.d("Download notification channel created")
        }
    }

    /**
     * Show download progress notification
     */
    fun showDownloadProgress(
        downloadId: Long,
        fileName: String,
        progress: Int,
        downloadedSize: Long,
        totalSize: Long
    ) {
        val notificationId = NOTIFICATION_ID_BASE + downloadId.toInt()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            // TODO: Add extra to navigate to downloads screen
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download)
            .setContentTitle(fileName)
            .setContentText("Downloading... ${formatFileSize(downloadedSize)} / ${formatFileSize(totalSize)}")
            .setProgress(100, progress, false)
            .setOngoing(true)
            .setContentIntent(pendingIntent)
            .setAutoCancel(false)
            .setPriority(NotificationCompat.PRIORITY_LOW)
            .build()

        try {
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to show download progress notification")
        }
    }

    /**
     * Show download completed notification
     */
    fun showDownloadCompleted(downloadId: Long, fileName: String) {
        val notificationId = NOTIFICATION_ID_BASE + downloadId.toInt()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_sys_download_done)
            .setContentTitle("Download complete")
            .setContentText(fileName)
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to show download completed notification")
        }
    }

    /**
     * Show download failed notification
     */
    fun showDownloadFailed(downloadId: Long, fileName: String, error: String?) {
        val notificationId = NOTIFICATION_ID_BASE + downloadId.toInt()

        val intent = Intent(context, MainActivity::class.java).apply {
            flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
        }

        val pendingIntent = PendingIntent.getActivity(
            context,
            notificationId,
            intent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )

        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(android.R.drawable.stat_notify_error)
            .setContentTitle("Download failed")
            .setContentText("$fileName - ${error ?: "Unknown error"}")
            .setAutoCancel(true)
            .setContentIntent(pendingIntent)
            .setPriority(NotificationCompat.PRIORITY_DEFAULT)
            .build()

        try {
            notificationManager.notify(notificationId, notification)
        } catch (e: SecurityException) {
            Timber.e(e, "Failed to show download failed notification")
        }
    }

    /**
     * Update download notification based on status
     */
    fun updateDownloadNotification(
        downloadId: Long,
        fileName: String,
        status: DownloadStatus,
        progress: Int = 0,
        downloadedSize: Long = 0,
        totalSize: Long = 0,
        errorMessage: String? = null
    ) {
        when (status) {
            DownloadStatus.DOWNLOADING -> {
                showDownloadProgress(downloadId, fileName, progress, downloadedSize, totalSize)
            }
            DownloadStatus.COMPLETED -> {
                showDownloadCompleted(downloadId, fileName)
            }
            DownloadStatus.FAILED -> {
                showDownloadFailed(downloadId, fileName, errorMessage)
            }
            DownloadStatus.CANCELLED -> {
                cancelNotification(downloadId)
            }
            DownloadStatus.PAUSED -> {
                // Keep existing notification, just update text
                showDownloadProgress(downloadId, fileName, progress, downloadedSize, totalSize)
            }
            else -> {
                // PENDING - don't show notification yet
            }
        }
    }

    /**
     * Cancel/dismiss a download notification
     */
    fun cancelNotification(downloadId: Long) {
        val notificationId = NOTIFICATION_ID_BASE + downloadId.toInt()
        notificationManager.cancel(notificationId)
        Timber.d("Cancelled notification for download $downloadId")
    }

    private fun formatFileSize(bytes: Long): String {
        if (bytes < 1024) return "$bytes B"
        val kb = bytes / 1024.0
        if (kb < 1024) return "%.1f KB".format(kb)
        val mb = kb / 1024.0
        if (mb < 1024) return "%.1f MB".format(mb)
        val gb = mb / 1024.0
        return "%.1f GB".format(gb)
    }
}