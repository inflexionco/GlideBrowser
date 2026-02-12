package com.inflexionco.glidebrowser.util

import android.content.Context
import android.graphics.Bitmap
import android.graphics.Canvas
import android.webkit.WebView
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import timber.log.Timber
import java.io.File
import java.io.FileOutputStream
import java.io.IOException

/**
 * Utility for capturing WebView thumbnails
 */
object WebViewThumbnailUtil {

    private const val THUMBNAIL_WIDTH = 400
    private const val THUMBNAIL_HEIGHT = 300
    private const val THUMBNAIL_QUALITY = 75
    private const val THUMBNAILS_DIR = "tab_thumbnails"

    /**
     * Capture a thumbnail from WebView
     * @return File path of saved thumbnail, or null if failed
     */
    suspend fun captureThumbnail(
        context: Context,
        webView: WebView,
        tabId: Long
    ): String? = withContext(Dispatchers.IO) {
        try {
            // Create bitmap from WebView on main thread
            val bitmap = withContext(Dispatchers.Main) {
                try {
                    // Enable drawing cache
                    webView.isDrawingCacheEnabled = true
                    webView.buildDrawingCache()

                    // Create bitmap from WebView
                    val width = webView.width
                    val height = webView.height

                    if (width <= 0 || height <= 0) {
                        Timber.w("WebView has invalid dimensions: ${width}x$height")
                        return@withContext null
                    }

                    val screenshot = Bitmap.createBitmap(width, height, Bitmap.Config.ARGB_8888)
                    val canvas = Canvas(screenshot)
                    webView.draw(canvas)

                    // Disable drawing cache
                    webView.isDrawingCacheEnabled = false

                    // Scale down to thumbnail size
                    val thumbnail = Bitmap.createScaledBitmap(
                        screenshot,
                        THUMBNAIL_WIDTH,
                        THUMBNAIL_HEIGHT,
                        true
                    )

                    // Recycle original
                    if (screenshot != thumbnail) {
                        screenshot.recycle()
                    }

                    thumbnail
                } catch (e: Exception) {
                    Timber.e(e, "Failed to capture WebView bitmap")
                    null
                }
            }

            if (bitmap == null) {
                return@withContext null
            }

            // Save to file
            val file = getThumbnailFile(context, tabId)
            val saved = saveBitmap(bitmap, file)

            // Recycle bitmap
            bitmap.recycle()

            if (saved) {
                Timber.d("Thumbnail saved: ${file.absolutePath}")
                file.absolutePath
            } else {
                null
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to capture thumbnail")
            null
        }
    }

    /**
     * Get thumbnail file for a tab
     */
    fun getThumbnailFile(context: Context, tabId: Long): File {
        val thumbnailsDir = File(context.filesDir, THUMBNAILS_DIR)
        if (!thumbnailsDir.exists()) {
            thumbnailsDir.mkdirs()
        }
        return File(thumbnailsDir, "tab_${tabId}.jpg")
    }

    /**
     * Delete thumbnail file
     */
    fun deleteThumbnail(context: Context, tabId: Long): Boolean {
        return try {
            val file = getThumbnailFile(context, tabId)
            if (file.exists()) {
                val deleted = file.delete()
                if (deleted) {
                    Timber.d("Thumbnail deleted: ${file.absolutePath}")
                }
                deleted
            } else {
                true // Already doesn't exist
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete thumbnail")
            false
        }
    }

    /**
     * Delete all thumbnails
     */
    fun deleteAllThumbnails(context: Context): Boolean {
        return try {
            val thumbnailsDir = File(context.filesDir, THUMBNAILS_DIR)
            if (thumbnailsDir.exists()) {
                thumbnailsDir.listFiles()?.forEach { it.delete() }
                thumbnailsDir.delete()
            }
            Timber.d("All thumbnails deleted")
            true
        } catch (e: Exception) {
            Timber.e(e, "Failed to delete all thumbnails")
            false
        }
    }

    /**
     * Check if thumbnail exists for a tab
     */
    fun thumbnailExists(context: Context, tabId: Long): Boolean {
        return getThumbnailFile(context, tabId).exists()
    }

    /**
     * Get thumbnail file path if it exists
     */
    fun getThumbnailPath(context: Context, tabId: Long): String? {
        val file = getThumbnailFile(context, tabId)
        return if (file.exists()) file.absolutePath else null
    }

    /**
     * Save bitmap to file
     */
    private fun saveBitmap(bitmap: Bitmap, file: File): Boolean {
        var outputStream: FileOutputStream? = null
        return try {
            outputStream = FileOutputStream(file)
            bitmap.compress(Bitmap.CompressFormat.JPEG, THUMBNAIL_QUALITY, outputStream)
            outputStream.flush()
            true
        } catch (e: IOException) {
            Timber.e(e, "Failed to save bitmap")
            false
        } finally {
            try {
                outputStream?.close()
            } catch (e: IOException) {
                Timber.e(e, "Failed to close output stream")
            }
        }
    }
}