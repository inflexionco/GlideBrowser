package com.inflexionco.glidebrowser.presentation.home

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.data.local.entity.FavoriteEntity
import com.inflexionco.glidebrowser.presentation.home.components.AddFavoriteCard
import com.inflexionco.glidebrowser.presentation.home.components.AddFavoriteDialog
import com.inflexionco.glidebrowser.presentation.home.components.WebsiteCard
import com.inflexionco.glidebrowser.ui.components.TvButton
import com.inflexionco.glidebrowser.ui.theme.Dimens

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HomeScreen(
    onNavigateToBrowser: (String?) -> Unit,
    onNavigateToBookmarks: () -> Unit = {},
    onNavigateToHistory: () -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()
    var showAddDialog by remember { mutableStateOf(false) }
    val firstCardFocusRequester = remember { FocusRequester() }

    // Request focus on first card when screen loads
    LaunchedEffect(uiState.favorites) {
        if (uiState.favorites.isNotEmpty()) {
            firstCardFocusRequester.requestFocus()
        }
    }

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

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
            ) {
                TvButton(
                    text = "Bookmarks",
                    onClick = onNavigateToBookmarks,
                    icon = Icons.Default.Star
                )
                TvButton(
                    text = "History",
                    onClick = onNavigateToHistory,
                    icon = Icons.AutoMirrored.Filled.List
                )
                TvButton(
                    text = "Open URL",
                    onClick = { onNavigateToBrowser("https://www.google.com") },
                    icon = Icons.Default.Search
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing32))

        // Quick access grid with TV-optimized spacing
        LazyVerticalGrid(
            columns = GridCells.Fixed(5),
            modifier = Modifier
                .fillMaxSize()
                .focusGroup(), // Group focus for better D-pad navigation
            contentPadding = PaddingValues(
                horizontal = Dimens.spacing8,
                vertical = Dimens.spacing8
            ),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing24), // Larger spacing for TV
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing24)
        ) {
            // Favorites
            items(uiState.favorites, key = { it.id }) { favorite ->
                val isFirst = favorite == uiState.favorites.firstOrNull()
                WebsiteCard(
                    title = favorite.title,
                    url = favorite.url,
                    onClick = { onNavigateToBrowser(favorite.url) },
                    onRemove = {
                        viewModel.removeFavorite(favorite.url)
                    },
                    modifier = if (isFirst) {
                        Modifier.focusRequester(firstCardFocusRequester)
                    } else {
                        Modifier
                    }
                )
            }

            // Add favorite card
            item {
                AddFavoriteCard(
                    onClick = { showAddDialog = true }
                )
            }
        }
    }

    // Add favorite dialog
    if (showAddDialog) {
        AddFavoriteDialog(
            onDismiss = { showAddDialog = false },
            onConfirm = { title, url ->
                // Ensure URL has protocol
                val finalUrl = if (!url.startsWith("http://") && !url.startsWith("https://")) {
                    "https://$url"
                } else {
                    url
                }
                viewModel.addFavorite(title, finalUrl)
                showAddDialog = false
            }
        )
    }
}