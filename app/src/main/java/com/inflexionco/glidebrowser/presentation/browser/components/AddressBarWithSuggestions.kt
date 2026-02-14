package com.inflexionco.glidebrowser.presentation.browser.components

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import com.inflexionco.glidebrowser.presentation.browser.SuggestionViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AddressBarWithSuggestions(
    url: String,
    isLoading: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int = 1,
    isBookmarked: Boolean = false,
    onUrlSubmit: (String) -> Unit,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onStopClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTabsClick: () -> Unit = {},
    onNewTabClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    onVoiceClick: () -> Unit = {},
    modifier: Modifier = Modifier,
    suggestionViewModel: SuggestionViewModel = hiltViewModel()
) {
    var currentQuery by remember { mutableStateOf("") }
    var showSuggestions by remember { mutableStateOf(false) }
    val suggestions by suggestionViewModel.getSuggestions(currentQuery)
        .collectAsState(initial = emptyList())

    Box(modifier = modifier) {
        Column {
            // Address Bar
            AddressBarInternal(
                url = url,
                isLoading = isLoading,
                canGoBack = canGoBack,
                canGoForward = canGoForward,
                tabCount = tabCount,
                isBookmarked = isBookmarked,
                onUrlSubmit = {
                    onUrlSubmit(it)
                    showSuggestions = false
                },
                onTextChanged = { query ->
                    currentQuery = query
                    showSuggestions = query.isNotBlank()
                },
                onBackClick = onBackClick,
                onForwardClick = onForwardClick,
                onRefreshClick = onRefreshClick,
                onStopClick = onStopClick,
                onHomeClick = onHomeClick,
                onTabsClick = onTabsClick,
                onNewTabClick = onNewTabClick,
                onBookmarkClick = onBookmarkClick,
                onMenuClick = onMenuClick,
                onVoiceClick = onVoiceClick
            )

            // URL Suggestions
            if (showSuggestions && suggestions.isNotEmpty()) {
                UrlSuggestionsList(
                    suggestions = suggestions,
                    onSuggestionClick = { selectedUrl ->
                        onUrlSubmit(selectedUrl)
                        showSuggestions = false
                    },
                    modifier = Modifier.padding(horizontal = 16.dp, vertical = 8.dp)
                )
            }
        }
    }
}