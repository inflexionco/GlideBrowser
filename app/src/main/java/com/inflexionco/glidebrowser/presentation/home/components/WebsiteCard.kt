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
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.Card
import androidx.tv.material3.CardDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.ui.theme.Dimens

/**
 * TV-optimized website card for quick access (like app icons in launcher)
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun WebsiteCard(
    title: String,
    url: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    Card(
        onClick = onClick,
        modifier = modifier
            .aspectRatio(1f),
        shape = CardDefaults.shape(
            shape = RoundedCornerShape(16.dp)
        ),
        colors = CardDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer
        ),
        scale = CardDefaults.scale(
            focusedScale = 1.05f
        ),
        border = CardDefaults.border(
            focusedBorder = Border(
                border = BorderStroke(
                    width = 3.dp,
                    color = MaterialTheme.colorScheme.primary
                ),
                shape = RoundedCornerShape(16.dp)
            )
        ),
        interactionSource = interactionSource
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.spacing16),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Icon placeholder (in future can be favicon)
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(12.dp))
                    .background(
                        if (isFocused) {
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.1f)
                        } else {
                            MaterialTheme.colorScheme.surface
                        }
                    ),
                contentAlignment = Alignment.Center
            ) {
                Icon(
                    imageVector = Icons.Default.Star,
                    contentDescription = null,
                    modifier = Modifier.size(48.dp),
                    tint = if (isFocused) {
                        MaterialTheme.colorScheme.primary
                    } else {
                        MaterialTheme.colorScheme.onSurfaceVariant
                    }
                )
            }

            // Title
            Text(
                text = title,
                style = MaterialTheme.typography.labelLarge,
                color = if (isFocused) {
                    MaterialTheme.colorScheme.onPrimaryContainer
                } else {
                    MaterialTheme.colorScheme.onSurfaceVariant
                },
                maxLines = 2,
                overflow = TextOverflow.Ellipsis,
                textAlign = TextAlign.Center,
                modifier = Modifier.padding(top = Dimens.spacing8)
            )
        }
    }
}
