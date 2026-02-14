package com.inflexionco.glidebrowser.presentation.downloads.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.CheckCircle
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.focus.onFocusChanged
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import com.inflexionco.glidebrowser.data.local.entity.DownloadStatus
import com.inflexionco.glidebrowser.domain.model.DownloadItem
import com.inflexionco.glidebrowser.ui.theme.Dimens
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DownloadItemCard(
    download: DownloadItem,
    onPause: () -> Unit,
    onResume: () -> Unit,
    onCancel: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    var isFocused by remember { mutableStateOf(false) }
    val scale = if (isFocused) Dimens.focusedScale else 1f

    Card(
        onClick = { /* Open file */ },
        modifier = modifier
            .fillMaxWidth()
            .height(120.dp)
            .scale(scale)
            .onFocusChanged { isFocused = it.isFocused },
        colors = CardDefaults.colors(
            containerColor = when (download.status) {
                DownloadStatus.COMPLETED -> MaterialTheme.colorScheme.surface
                DownloadStatus.FAILED -> MaterialTheme.colorScheme.errorContainer.copy(alpha = 0.3f)
                else -> MaterialTheme.colorScheme.surface
            }
        ),
        shape = CardDefaults.shape(shape = MaterialTheme.shapes.medium),
        scale = CardDefaults.scale(focusedScale = 1f),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = androidx.compose.foundation.BorderStroke(
                    width = Dimens.focusBorderWidth,
                    color = MaterialTheme.colorScheme.primary
                ),
                shape = MaterialTheme.shapes.medium
            )
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.spacing16),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Download info
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing8)
            ) {
                // File name
                Text(
                    text = download.fileName,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )

                // Status and progress
                Row(
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12),
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    DownloadStatusChip(download.status)

                    if (download.status == DownloadStatus.DOWNLOADING ||
                        download.status == DownloadStatus.PAUSED) {
                        Text(
                            text = "${download.progressPercentage}%",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.primary
                        )
                    }
                }

                // Progress indicator (simple box)
                if (download.status == DownloadStatus.DOWNLOADING ||
                    download.status == DownloadStatus.PAUSED) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(6.dp)
                            .background(
                                MaterialTheme.colorScheme.surfaceVariant,
                                shape = MaterialTheme.shapes.extraSmall
                            )
                    ) {
                        Box(
                            modifier = Modifier
                                .fillMaxWidth(download.progress)
                                .fillMaxHeight()
                                .background(
                                    MaterialTheme.colorScheme.primary,
                                    shape = MaterialTheme.shapes.extraSmall
                                )
                        )
                    }
                }

                // File size or date
                Text(
                    text = if (download.isComplete) {
                        "Completed ${formatDate(download.completedAt)}"
                    } else {
                        "${formatFileSize(download.downloadedSize)} / ${formatFileSize(download.fileSize)}"
                    },
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )

                // Error message
                if (download.status == DownloadStatus.FAILED && download.errorMessage != null) {
                    Text(
                        text = download.errorMessage,
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.error,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis
                    )
                }
            }

            Spacer(modifier = Modifier.width(Dimens.spacing16))

            // Action buttons
            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
            ) {
                when (download.status) {
                    DownloadStatus.DOWNLOADING -> {
                        IconButton(onClick = onPause) {
                            Text(text = "Pause", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = onCancel) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    DownloadStatus.PAUSED -> {
                        IconButton(onClick = onResume) {
                            Text(text = "Resume", style = MaterialTheme.typography.bodySmall)
                        }
                        IconButton(onClick = onCancel) {
                            Icon(
                                imageVector = Icons.Default.Close,
                                contentDescription = "Cancel",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    DownloadStatus.COMPLETED,
                    DownloadStatus.FAILED,
                    DownloadStatus.CANCELLED -> {
                        IconButton(onClick = onDelete) {
                            Icon(
                                imageVector = Icons.Default.Delete,
                                contentDescription = "Delete",
                                tint = MaterialTheme.colorScheme.error
                            )
                        }
                    }
                    else -> {}
                }
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun DownloadStatusChip(status: DownloadStatus) {
    val (text, color) = when (status) {
        DownloadStatus.PENDING -> Pair("Pending", Color.Gray)
        DownloadStatus.DOWNLOADING -> Pair("Downloading", MaterialTheme.colorScheme.primary)
        DownloadStatus.PAUSED -> Pair("Paused", Color.Gray)
        DownloadStatus.COMPLETED -> Pair("Completed", Color(0xFF4CAF50))
        DownloadStatus.FAILED -> Pair("Failed", MaterialTheme.colorScheme.error)
        DownloadStatus.CANCELLED -> Pair("Cancelled", Color.Gray)
    }

    Box(
        modifier = Modifier
            .height(24.dp)
            .background(
                color.copy(alpha = 0.15f),
                MaterialTheme.shapes.small
            )
            .padding(horizontal = 8.dp, vertical = 4.dp)
    ) {
        Text(
            text = text,
            style = MaterialTheme.typography.labelSmall,
            color = color
        )
    }
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

private fun formatDate(timestamp: Long?): String {
    if (timestamp == null) return ""
    val date = Date(timestamp)
    val format = SimpleDateFormat("MMM dd, HH:mm", Locale.getDefault())
    return format.format(date)
}