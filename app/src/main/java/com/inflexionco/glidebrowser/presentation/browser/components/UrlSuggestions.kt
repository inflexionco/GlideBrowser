package com.inflexionco.glidebrowser.presentation.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.*
import coil.compose.AsyncImage
import com.inflexionco.glidebrowser.ui.theme.Dimens
import java.net.URL

data class UrlSuggestion(
    val title: String,
    val url: String,
    val source: SuggestionSource
)

enum class SuggestionSource {
    HISTORY,
    BOOKMARK
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun UrlSuggestionsList(
    suggestions: List<UrlSuggestion>,
    onSuggestionClick: (String) -> Unit,
    modifier: Modifier = Modifier
) {
    if (suggestions.isEmpty()) return

    Box(
        modifier = modifier
            .fillMaxWidth()
            .heightIn(max = 300.dp)
            .background(
                color = MaterialTheme.colorScheme.surfaceVariant,
                shape = RoundedCornerShape(12.dp)
            )
    ) {
        LazyColumn(
            modifier = Modifier
                .fillMaxWidth()
                .focusGroup(),
            contentPadding = PaddingValues(Dimens.spacing8),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing4)
        ) {
            items(suggestions.take(5), key = { it.url }) { suggestion ->
                SuggestionItem(
                    suggestion = suggestion,
                    onClick = { onSuggestionClick(suggestion.url) }
                )
            }
        }
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun SuggestionItem(
    suggestion: UrlSuggestion,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .height(60.dp),
        shape = CardDefaults.shape(shape = RoundedCornerShape(8.dp)),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        scale = CardDefaults.scale(
            scale = 1f,
            focusedScale = 1.02f
        )
    ) {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Dimens.spacing12, vertical = Dimens.spacing8),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing12),
            verticalAlignment = Alignment.CenterVertically
        ) {
            // Favicon
            val domain = try {
                URL(suggestion.url).host
            } catch (e: Exception) {
                ""
            }

            AsyncImage(
                model = "https://www.google.com/s2/favicons?domain=$domain&sz=48",
                contentDescription = null,
                modifier = Modifier.size(24.dp)
            )

            // Title and URL
            Column(
                modifier = Modifier.weight(1f),
                verticalArrangement = Arrangement.Center
            ) {
                Text(
                    text = suggestion.title,
                    style = MaterialTheme.typography.titleSmall,
                    color = MaterialTheme.colorScheme.onSurface,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
                Spacer(modifier = Modifier.height(2.dp))
                Text(
                    text = suggestion.url,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis
                )
            }

            // Source indicator
            Text(
                text = when (suggestion.source) {
                    SuggestionSource.HISTORY -> "History"
                    SuggestionSource.BOOKMARK -> "Bookmark"
                },
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}