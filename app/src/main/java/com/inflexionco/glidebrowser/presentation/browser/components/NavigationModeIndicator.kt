package com.inflexionco.glidebrowser.presentation.browser.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.domain.model.NavigationMode

/**
 * Visual indicator showing the current navigation mode
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun NavigationModeIndicator(
    mode: NavigationMode,
    isVisible: Boolean,
    elementCount: Int = 0,
    focusedIndex: Int = -1,
    modifier: Modifier = Modifier
) {
    AnimatedVisibility(
        visible = isVisible,
        enter = fadeIn(),
        exit = fadeOut(),
        modifier = modifier
    ) {
        Box(
            modifier = Modifier
                .background(
                    color = Color.Black.copy(alpha = 0.7f),
                    shape = RoundedCornerShape(8.dp)
                )
                .padding(horizontal = 16.dp, vertical = 12.dp)
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically
            ) {
                // Mode icon/indicator
                Box(
                    modifier = Modifier
                        .size(12.dp)
                        .background(
                            color = when (mode) {
                                NavigationMode.FOCUS -> Color(0xFF4CAF50) // Green
                                NavigationMode.SCROLL -> Color(0xFF2196F3) // Blue
                            },
                            shape = RoundedCornerShape(6.dp)
                        )
                )

                // Mode text
                Text(
                    text = when (mode) {
                        NavigationMode.FOCUS -> "FOCUS MODE"
                        NavigationMode.SCROLL -> "SCROLL MODE"
                    },
                    style = MaterialTheme.typography.labelLarge,
                    color = Color.White,
                    modifier = Modifier.padding(start = 12.dp)
                )

                // Element counter (only in Focus mode)
                if (mode == NavigationMode.FOCUS && elementCount > 0) {
                    Text(
                        text = if (focusedIndex >= 0) {
                            " • ${focusedIndex + 1}/$elementCount"
                        } else {
                            " • $elementCount elements"
                        },
                        style = MaterialTheme.typography.labelMedium,
                        color = Color.White.copy(alpha = 0.8f),
                        modifier = Modifier.padding(start = 4.dp)
                    )
                }
            }
        }
    }
}