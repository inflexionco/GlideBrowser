package com.inflexionco.glidebrowser.presentation.history.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Delete
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import com.inflexionco.glidebrowser.domain.model.HistoryItem
import com.inflexionco.glidebrowser.ui.theme.Dimens
import java.net.URL
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HistoryItemCard(
    historyItem: HistoryItem,
    onClick: () -> Unit,
    onDelete: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        onLongClick = onDelete,
        modifier = modifier
            .fillMaxWidth()
            .height(80.dp),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.surface
        ),
        scale = CardDefaults.scale(
            scale = 1f,
            focusedScale = 1.02f
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(3.dp, MaterialTheme.colorScheme.primary)
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
            // Favicon
            val domain = try {
                URL(historyItem.url).host
            } catch (e: Exception) {
                ""
            }

            AsyncImage(
                model = "https://www.google.com/s2/favicons?domain=$domain&sz=48",
                contentDescription = null,
                modifier = Modifier.size(32.dp)
            )

            Spacer(modifier = Modifier.width(Dimens.spacing16))

            // Title and URL
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = historyItem.title,
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = historyItem.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            Spacer(modifier = Modifier.width(Dimens.spacing16))

            // Visit count and time
            Column(
                horizontalAlignment = Alignment.End,
                verticalArrangement = Arrangement.Center
            ) {
                if (historyItem.visitCount > 1) {
                    Text(
                        text = "${historyItem.visitCount} visits",
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.primary
                    )
                }
                Text(
                    text = formatTime(historyItem.visitedAt),
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                )
            }
        }
    }
}

private fun formatTime(timestamp: Long): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }

    return if (calendar.timeInMillis >= today.timeInMillis) {
        // Today - show time
        SimpleDateFormat("h:mm a", Locale.getDefault()).format(Date(timestamp))
    } else {
        // Other days - show date
        SimpleDateFormat("MMM d", Locale.getDefault()).format(Date(timestamp))
    }
}