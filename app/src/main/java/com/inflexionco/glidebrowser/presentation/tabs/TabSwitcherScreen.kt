package com.inflexionco.glidebrowser.presentation.tabs

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.presentation.tabs.components.TabCard
import com.inflexionco.glidebrowser.ui.components.TvButton
import com.inflexionco.glidebrowser.ui.components.TvLoadingIndicator
import com.inflexionco.glidebrowser.ui.theme.Dimens

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TabSwitcherScreen(
    onTabSelect: (Long) -> Unit,
    onNewTab: () -> Unit,
    modifier: Modifier = Modifier,
    tabViewModel: TabViewModel = hiltViewModel()
) {
    val state by tabViewModel.state.collectAsState()
    val tabs = state.tabs
    val activeTab = state.activeTab

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.spacing24)
    ) {
        // Header
        Text(
            text = "Tabs (${tabs.size})",
            style = MaterialTheme.typography.headlineMedium,
            color = MaterialTheme.colorScheme.onBackground,
            modifier = Modifier.padding(bottom = Dimens.spacing24)
        )

        when {
            state.isLoading -> {
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    TvLoadingIndicator(message = "Loading tabs...")
                }
            }

            tabs.isEmpty() -> {
                // Empty state
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Column(
                        horizontalAlignment = Alignment.CenterHorizontally,
                        verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
                    ) {
                        Text(
                            text = "No tabs open",
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.onBackground
                        )
                        Text(
                            text = "Create a new tab to get started",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f)
                        )
                        TvButton(
                            text = "New Tab",
                            onClick = onNewTab,
                            icon = Icons.Default.Add,
                            modifier = Modifier.padding(top = Dimens.spacing16)
                        )
                    }
                }
            }

            else -> {
                // Tab grid
                LazyVerticalGrid(
                    columns = GridCells.Fixed(3),
                    modifier = Modifier.fillMaxSize(),
                    contentPadding = PaddingValues(Dimens.spacing8),
                    horizontalArrangement = Arrangement.spacedBy(Dimens.spacing16),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
                ) {
                    // New tab button as first item
                    item {
                        TvButton(
                            text = "New Tab",
                            onClick = onNewTab,
                            icon = Icons.Default.Add,
                            modifier = Modifier
                                .fillMaxWidth()
                                .padding(Dimens.spacing8)
                        )
                    }

                    // Tab cards
                    items(tabs, key = { it.id }) { tab ->
                        TabCard(
                            tab = tab,
                            isActive = tab.id == activeTab?.id,
                            onClick = { onTabSelect(tab.id) },
                            onClose = { tabViewModel.closeTab(tab.id) }
                        )
                    }
                }
            }
        }
    }

    // Show error if any
    state.error?.let { error ->
        Box(
            modifier = Modifier
                .fillMaxSize()
                .padding(Dimens.spacing16),
            contentAlignment = Alignment.BottomCenter
        ) {
            Text(
                text = error,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.error,
                modifier = Modifier
                    .background(
                        color = MaterialTheme.colorScheme.errorContainer,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(Dimens.spacing16)
            )
        }
    }
}