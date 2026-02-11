package com.inflexionco.glidebrowser.presentation.bookmarks.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import coil.compose.AsyncImage
import com.inflexionco.glidebrowser.ui.theme.Dimens

/**
 * TV-optimized bookmark card with edit action
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BookmarkCard(
    title: String,
    url: String,
    onClick: () -> Unit,
    onEdit: () -> Unit,
    onRemove: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Extract domain for favicon
    val domain = remember(url) {
        try {
            val uri = java.net.URI(url)
            "${uri.scheme}://${uri.host}"
        } catch (e: Exception) {
            url
        }
    }

    // Use Google's favicon service
    val faviconUrl = remember(domain) {
        "https://www.google.com/s2/favicons?domain=$domain&sz=128"
    }

    Card(
        onClick = onClick,
        onLongClick = onEdit, // Edit on long-press
        modifier = modifier
            .aspectRatio(1.6f),
        shape = CardDefaults.shape(
            shape = RoundedCornerShape(12.dp)
        ),
        colors = CardDefaults.colors(
            containerColor = Color.White,
            focusedContainerColor = Color.White,
            pressedContainerColor = Color.White.copy(alpha = 0.9f)
        ),
        scale = CardDefaults.scale(
            focusedScale = 1.1f
        ),
        border = CardDefaults.border(
            border = Border(
                border = BorderStroke(
                    width = 0.dp,
                    color = Color.Transparent
                ),
                shape = RoundedCornerShape(12.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(12.dp)
            )
        ),
        glow = CardDefaults.glow(
            focusedGlow = androidx.tv.material3.Glow(
                elevationColor = MaterialTheme.colorScheme.primary,
                elevation = 8.dp
            )
        ),
        interactionSource = interactionSource
    ) {
        Box(
            modifier = Modifier
                .fillMaxSize()
                .background(Color.White),
            contentAlignment = Alignment.Center
        ) {
            // Website logo/favicon
            AsyncImage(
                model = faviconUrl,
                contentDescription = title,
                modifier = Modifier
                    .fillMaxWidth(0.7f)
                    .aspectRatio(1f),
                contentScale = ContentScale.Fit
            )

            // Edit indicator when focused
            if (isFocused) {
                Icon(
                    imageVector = Icons.Default.Edit,
                    contentDescription = "Long-press to edit",
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(Dimens.spacing8)
                        .size(20.dp),
                    tint = MaterialTheme.colorScheme.primary.copy(alpha = 0.7f)
                )
            }
        }
    }
}