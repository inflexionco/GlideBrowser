package com.inflexionco.glidebrowser.presentation.downloads

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Tab
import androidx.tv.material3.TabRow
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.presentation.downloads.components.DownloadItemCard
import com.inflexionco.glidebrowser.ui.components.TvButton
import com.inflexionco.glidebrowser.ui.theme.Dimens

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun DownloadsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: DownloadViewModel = hiltViewModel()
) {
    val downloads by viewModel.downloads.collectAsState()
    val activeDownloads by viewModel.activeDownloads.collectAsState()
    val selectedFilter by viewModel.selectedFilter.collectAsState()
    val backButtonFocusRequester = remember { FocusRequester() }

    // Request focus on back button when screen loads
    LaunchedEffect(Unit) {
        backButtonFocusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.spacing24)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Downloads",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${downloads.size} total, ${activeDownloads.size} active",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Dimens.spacing4)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
            ) {
                TvButton(
                    text = "Clear Completed",
                    onClick = { viewModel.clearCompletedDownloads() },
                    enabled = downloads.any { it.isComplete }
                )
                TvButton(
                    text = "Back",
                    onClick = onNavigateBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    modifier = Modifier.focusRequester(backButtonFocusRequester)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing24))

        // Filter tabs
        TabRow(
            selectedTabIndex = selectedFilter.ordinal,
            modifier = Modifier.fillMaxWidth(),
            separator = { Spacer(modifier = Modifier.width(Dimens.spacing8)) }
        ) {
            DownloadFilter.entries.forEach { filter ->
                Tab(
                    selected = selectedFilter == filter,
                    onFocus = { viewModel.setFilter(filter) }
                ) {
                    Text(
                        text = when (filter) {
                            DownloadFilter.ALL -> "All (${downloads.size})"
                            DownloadFilter.ACTIVE -> "Active (${activeDownloads.size})"
                            DownloadFilter.COMPLETED -> "Completed"
                            DownloadFilter.FAILED -> "Failed"
                        },
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.padding(horizontal = 16.dp, vertical = 12.dp)
                    )
                }
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing24))

        // Downloads list
        if (downloads.isEmpty()) {
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
                        text = when (selectedFilter) {
                            DownloadFilter.ALL -> "No Downloads Yet"
                            DownloadFilter.ACTIVE -> "No Active Downloads"
                            DownloadFilter.COMPLETED -> "No Completed Downloads"
                            DownloadFilter.FAILED -> "No Failed Downloads"
                        },
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Downloaded files will appear here",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyColumn(
                modifier = Modifier
                    .fillMaxSize()
                    .focusGroup(),
                contentPadding = PaddingValues(
                    horizontal = Dimens.spacing8,
                    vertical = Dimens.spacing8
                ),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
            ) {
                items(downloads, key = { it.id }) { download ->
                    DownloadItemCard(
                        download = download,
                        onPause = { viewModel.pauseDownload(download.id) },
                        onResume = { viewModel.resumeDownload(download.id) },
                        onCancel = { viewModel.cancelDownload(download.id) },
                        onDelete = { viewModel.deleteDownload(download.id) }
                    )
                }
            }
        }
    }
}