package com.inflexionco.glidebrowser.ui.components

import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Shape
import androidx.tv.material3.Border
import androidx.tv.material3.Button
import androidx.tv.material3.ButtonDefaults
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.ui.theme.Dimens
import androidx.compose.ui.graphics.vector.ImageVector

/**
 * TV-optimized button with focus management and TV-specific styling
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null,
    shape: Shape = MaterialTheme.shapes.medium,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        shape = ButtonDefaults.shape(shape),
        colors = ButtonDefaults.colors(
            containerColor = MaterialTheme.colorScheme.primary,
            contentColor = MaterialTheme.colorScheme.onPrimary,
            focusedContainerColor = MaterialTheme.colorScheme.primaryContainer,
            focusedContentColor = MaterialTheme.colorScheme.onPrimaryContainer
        ),
        scale = ButtonDefaults.scale(
            focusedScale = Dimens.focusedScale
        ),
        border = ButtonDefaults.border(
            focusedBorder = Border(
                border = androidx.compose.foundation.BorderStroke(
                    width = Dimens.focusBorderWidth,
                    color = MaterialTheme.colorScheme.border
                )
            )
        ),
        glow = ButtonDefaults.glow(
            focusedGlow = androidx.tv.material3.Glow(
                elevationColor = MaterialTheme.colorScheme.primary,
                elevation = Dimens.focusedElevation
            )
        ),
        contentPadding = PaddingValues(
            horizontal = Dimens.spacing24,
            vertical = Dimens.spacing16
        ),
        interactionSource = interactionSource
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null,
                modifier = Modifier
            )
            Spacer(modifier = Modifier.width(Dimens.spacing8))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelLarge
        )
    }
}

/**
 * Compact TV button variant for secondary actions
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvCompactButton(
    onClick: () -> Unit,
    text: String,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    icon: ImageVector? = null
) {
    val interactionSource = remember { MutableInteractionSource() }

    Button(
        onClick = onClick,
        modifier = modifier,
        enabled = enabled,
        colors = ButtonDefaults.colors(
            containerColor = MaterialTheme.colorScheme.surfaceVariant,
            contentColor = MaterialTheme.colorScheme.onSurface,
            focusedContainerColor = MaterialTheme.colorScheme.surface,
            focusedContentColor = MaterialTheme.colorScheme.onSurface
        ),
        scale = ButtonDefaults.scale(
            focusedScale = Dimens.focusedScale
        ),
        border = ButtonDefaults.border(
            focusedBorder = Border(
                border = androidx.compose.foundation.BorderStroke(
                    width = Dimens.focusBorderWidth,
                    color = MaterialTheme.colorScheme.border
                )
            )
        ),
        contentPadding = PaddingValues(
            horizontal = Dimens.spacing16,
            vertical = Dimens.spacing12
        ),
        interactionSource = interactionSource
    ) {
        if (icon != null) {
            Icon(
                imageVector = icon,
                contentDescription = null
            )
            Spacer(modifier = Modifier.width(Dimens.spacing8))
        }
        Text(
            text = text,
            style = MaterialTheme.typography.labelMedium
        )
    }
}