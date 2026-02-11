package com.inflexionco.glidebrowser.presentation.history

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
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
import com.inflexionco.glidebrowser.domain.model.HistoryItem
import com.inflexionco.glidebrowser.presentation.browser.HistoryViewModel
import com.inflexionco.glidebrowser.presentation.history.components.HistoryItemCard
import com.inflexionco.glidebrowser.ui.components.TvButton
import com.inflexionco.glidebrowser.ui.theme.Dimens
import java.text.SimpleDateFormat
import java.util.*

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun HistoryScreen(
    onNavigateBack: () -> Unit,
    onHistoryItemClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HistoryViewModel = hiltViewModel()
) {
    val historyItems by viewModel.getRecentHistory(100).collectAsState(initial = emptyList())
    val backButtonFocusRequester = remember { FocusRequester() }

    // Group history by date
    val groupedHistory = remember(historyItems) {
        historyItems.groupBy { item ->
            val calendar = Calendar.getInstance().apply {
                timeInMillis = item.visitedAt
            }
            calendar.apply {
                set(Calendar.HOUR_OF_DAY, 0)
                set(Calendar.MINUTE, 0)
                set(Calendar.SECOND, 0)
                set(Calendar.MILLISECOND, 0)
            }.timeInMillis
        }.toSortedMap(reverseOrder())
    }

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
                    text = "History",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${historyItems.size} ${if (historyItems.size == 1) "item" else "items"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Dimens.spacing4)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
            ) {
                TvButton(
                    text = "Clear All",
                    onClick = { viewModel.clearAllHistory() }
                )
                TvButton(
                    text = "Back",
                    onClick = onNavigateBack,
                    icon = Icons.Default.ArrowBack,
                    modifier = Modifier.focusRequester(backButtonFocusRequester)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing32))

        // History list with date grouping
        if (historyItems.isEmpty()) {
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
                        text = "No History Yet",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Pages you visit will appear here",
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
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing8)
            ) {
                groupedHistory.forEach { (dateTimestamp, items) ->
                    // Date header
                    item(key = "header_$dateTimestamp") {
                        Text(
                            text = formatDateHeader(dateTimestamp),
                            style = MaterialTheme.typography.titleLarge,
                            color = MaterialTheme.colorScheme.primary,
                            modifier = Modifier.padding(
                                top = Dimens.spacing16,
                                bottom = Dimens.spacing8
                            )
                        )
                    }

                    // History items for this date
                    items(items, key = { it.id }) { historyItem ->
                        HistoryItemCard(
                            historyItem = historyItem,
                            onClick = { onHistoryItemClick(historyItem.url) },
                            onDelete = { viewModel.deleteHistoryItem(historyItem) }
                        )
                    }
                }
            }
        }
    }
}

private fun formatDateHeader(timestamp: Long): String {
    val calendar = Calendar.getInstance().apply {
        timeInMillis = timestamp
    }
    val today = Calendar.getInstance().apply {
        set(Calendar.HOUR_OF_DAY, 0)
        set(Calendar.MINUTE, 0)
        set(Calendar.SECOND, 0)
        set(Calendar.MILLISECOND, 0)
    }
    val yesterday = Calendar.getInstance().apply {
        timeInMillis = today.timeInMillis
        add(Calendar.DAY_OF_YEAR, -1)
    }

    return when {
        calendar.timeInMillis == today.timeInMillis -> "Today"
        calendar.timeInMillis == yesterday.timeInMillis -> "Yesterday"
        else -> SimpleDateFormat("EEEE, MMMM d, yyyy", Locale.getDefault()).format(Date(timestamp))
    }
}