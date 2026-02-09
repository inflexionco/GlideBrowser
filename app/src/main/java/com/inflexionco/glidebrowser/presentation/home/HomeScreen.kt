package com.inflexionco.glidebrowser.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.presentation.home.components.WebsiteCard
import com.inflexionco.glidebrowser.ui.components.TvButton
import com.inflexionco.glidebrowser.ui.theme.Dimens

data class QuickAccessSite(
    val title: String,
    val url: String
)

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBrowser: (String?) -> Unit,
    modifier: Modifier = Modifier
) {
    // Popular websites for quick access
    val quickAccessSites = listOf(
        QuickAccessSite("YouTube", "https://www.youtube.com"),
        QuickAccessSite("Netflix", "https://www.netflix.com"),
        QuickAccessSite("Prime Video", "https://www.primevideo.com"),
        QuickAccessSite("Disney+", "https://www.disneyplus.com"),
        QuickAccessSite("Google", "https://www.google.com"),
        QuickAccessSite("Wikipedia", "https://www.wikipedia.org"),
        QuickAccessSite("Reddit", "https://www.reddit.com"),
        QuickAccessSite("Twitter", "https://www.twitter.com"),
        QuickAccessSite("Facebook", "https://www.facebook.com"),
        QuickAccessSite("Instagram", "https://www.instagram.com")
    )

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.spacing24)
    ) {
        // Header
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Glide Browser",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Quick Access",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Dimens.spacing4)
                )
            }

            TvButton(
                text = "Open URL",
                onClick = { onNavigateToBrowser("https://www.google.com") },
                icon = Icons.Default.Search
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacing32))

        // Quick access grid
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier.fillMaxSize(),
            contentPadding = PaddingValues(Dimens.spacing8),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
        ) {
            items(quickAccessSites) { site ->
                WebsiteCard(
                    title = site.title,
                    url = site.url,
                    onClick = { onNavigateToBrowser(site.url) }
                )
            }
        }
    }
}