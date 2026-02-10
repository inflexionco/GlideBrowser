package com.inflexionco.glidebrowser.presentation.home.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.background
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import coil.compose.AsyncImage
import com.inflexionco.glidebrowser.ui.theme.Dimens

/**
 * TV-optimized website card for quick access (like app icons in launcher)
 * Uses website favicons/logos for visual representation
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WebsiteCard(
    title: String,
    url: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    onRemove: (() -> Unit)? = null,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()
    var showRemoveOption by remember { mutableStateOf(false) }

    // Extract domain for favicon
    val domain = remember(url) {
        try {
            val uri = java.net.URI(url)
            "${uri.scheme}://${uri.host}"
        } catch (e: Exception) {
            url
        }
    }

    // Use Google's favicon service as fallback
    val faviconUrl = remember(domain) {
        "https://www.google.com/s2/favicons?domain=$domain&sz=128"
    }

    Card(
        onClick = onClick,
        onLongClick = onRemove?.let {
            {
                onRemove()
            }
        },
        modifier = modifier
            .aspectRatio(1.6f), // Wider aspect ratio like in reference
        shape = CardDefaults.shape(
            shape = RoundedCornerShape(12.dp)
        ),
        colors = CardDefaults.colors(
            containerColor = Color.White,
            focusedContainerColor = Color.White,
            pressedContainerColor = Color.White.copy(alpha = 0.9f)
        ),
        scale = CardDefaults.scale(
            focusedScale = 1.1f // Slightly more pronounced for TV visibility
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
                    color = MaterialTheme.colorScheme.primary // More visible focus indicator
                ),
                shape = RoundedCornerShape(12.dp)
            )
        ),
        glow = CardDefaults.glow(
            focusedGlow = androidx.tv.material3.Glow(
                elevationColor = MaterialTheme.colorScheme.primary,
                elevation = 8.dp // Add glow effect for better focus visibility
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
        }
    }
}
