package com.inflexionco.glidebrowser.presentation.browser.components

import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun PageLoadingIndicator(
    progress: Int,
    isVisible: Boolean,
    modifier: Modifier = Modifier
) {
    if (isVisible && progress < 100) {
        val animatedProgress by animateFloatAsState(
            targetValue = progress / 100f,
            animationSpec = tween(durationMillis = 300),
            label = "progress"
        )

        LinearProgressIndicator(
            progress = { animatedProgress },
            modifier = modifier
                .fillMaxWidth()
                .height(3.dp),
            color = MaterialTheme.colorScheme.primary,
            trackColor = MaterialTheme.colorScheme.surfaceVariant,
        )
    } else if (isVisible) {
        // Show empty space to maintain layout
        Box(
            modifier = modifier
                .fillMaxWidth()
                .height(3.dp)
        )
    }
}