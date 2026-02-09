package com.inflexionco.glidebrowser.ui.components

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.Border
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.IconButton
import androidx.tv.material3.IconButtonDefaults
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.ui.theme.Dimens

/**
 * TV-optimized tab icon button with tab count badge (like Chrome)
 * Displays a square icon with rounded corners and the number of tabs inside
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TvTabIconButton(
    onClick: () -> Unit,
    tabCount: Int,
    contentDescription: String?,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    interactionSource: MutableInteractionSource = remember { MutableInteractionSource() }
) {
    val isFocused by interactionSource.collectIsFocusedAsState()

    // Limit display to 99+ for very large numbers
    val displayCount = if (tabCount > 99) "99+" else tabCount.toString()

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
            shape = RoundedCornerShape(8.dp),
            focusedShape = RoundedCornerShape(8.dp),
            pressedShape = RoundedCornerShape(8.dp),
            disabledShape = RoundedCornerShape(8.dp)
        ),
        scale = IconButtonDefaults.scale(
            focusedScale = Dimens.focusedScale
        ),
        border = IconButtonDefaults.border(
            border = Border(
                border = BorderStroke(
                    width = 1.5.dp,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                ),
                shape = RoundedCornerShape(8.dp)
            ),
            focusedBorder = Border(
                border = BorderStroke(
                    width = Dimens.focusBorderWidth,
                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.3f)
                ),
                shape = RoundedCornerShape(8.dp)
            )
        ),
        interactionSource = interactionSource
    ) {
        Box(
            contentAlignment = Alignment.Center,
            modifier = Modifier.size(Dimens.iconSizeSmall)
        ) {
            Text(
                text = displayCount,
                style = MaterialTheme.typography.labelMedium,
                color = if (isFocused) {
                    MaterialTheme.colorScheme.onSurface
                } else {
                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.7f)
                }
            )
        }
    }
}
