package com.inflexionco.glidebrowser.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.ui.components.TvButton
import com.inflexionco.glidebrowser.ui.theme.Dimens

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBrowser: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Auto-navigate to browser on launch
    LaunchedEffect(Unit) {
        onNavigateToBrowser("https://www.google.com")
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background),
        contentAlignment = Alignment.Center
    ) {
        Column(
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing24)
        ) {
            Text(
                text = "Glide Browser",
                style = MaterialTheme.typography.displayMedium,
                color = MaterialTheme.colorScheme.onBackground
            )
            Text(
                text = "Fast, Modern Browser for Android TV",
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
            )

            Spacer(modifier = Modifier.height(Dimens.spacing32))

            TvButton(
                text = "Start Browsing",
                onClick = { onNavigateToBrowser("https://www.google.com") },
                icon = Icons.Default.Search,
                modifier = Modifier.padding(Dimens.spacing16)
            )
        }
    }
}