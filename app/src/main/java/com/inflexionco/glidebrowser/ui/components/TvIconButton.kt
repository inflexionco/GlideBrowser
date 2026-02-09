package com.inflexionco.glidebrowser.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.tv.material3.Border
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.MaterialTheme
import com.inflexionco.glidebrowser.ui.theme.Dimens

/**
 * TV-optimized icon button with focus indication
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    IconButton(
        onClick = onClick,
        modifier = modifier.size(Dimens.iconSize),
        enabled = enabled,
        colors = IconButtonDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surface,
            contentColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f),
            focusedContainerColor = MaterialTheme.colorScheme.surfaceVariant,
            focusedContentColor = MaterialTheme.colorScheme.onSurface
        ),
        shape = IconButtonDefaults.shape(
            shape = CircleShape,
            focusedShape = CircleShape,
            pressedShape = CircleShape,
            disabledShape = CircleShape
        ),
        scale = IconButtonDefaults.scale(
            focusedScale = Dimens.focusedScale
        ),
        border = IconButtonDefaults.border(
            focusedBorder = Border(
                border = androidx.compose.foundation.BorderStroke(
                    width = Dimens.focusBorderWidth,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ),
                shape = CircleShape
            )
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(Dimens.iconSizeSmall)
        )
    }
}

/**
 * Large icon button variant for primary actions
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvLargeIconButton(
    onClick: () -> Unit,
    icon: ImageVector,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    IconButton(
        onClick = onClick,
        modifier = modifier.size(Dimens.iconSizeLarge),
        enabled = enabled,
        colors = IconButtonDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        scale = IconButtonDefaults.scale(
            focusedScale = Dimens.focusedScale
        ),
        border = IconButtonDefaults.border(
            focusedBorder = Border(
                border = androidx.compose.foundation.BorderStroke(
                    width = Dimens.focusBorderWidth,
                    color = MaterialTheme.colorScheme.border
                )
            )
        ),
        glow = IconButtonDefaults.glow(
            focusedGlow = androidx.tv.material3.Glow(
                elevationColor = MaterialTheme.colorScheme.primary,
                elevation = Dimens.focusedElevation
            )
        ),
        interactionSource = interactionSource
    ) {
        Icon(
            imageVector = icon,
            contentDescription = contentDescription,
            modifier = Modifier.size(Dimens.iconSize)
        )
    }
}