package com.inflexionco.glidebrowser.presentation.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.inflexionco.glidebrowser.domain.model.WebViewEvent
import com.inflexionco.glidebrowser.presentation.browser.components.AddressBarWithSuggestions
import com.inflexionco.glidebrowser.presentation.browser.components.GlideWebView
import com.inflexionco.glidebrowser.presentation.browser.components.PageLoadingIndicator
import com.inflexionco.glidebrowser.presentation.tabs.TabViewModel

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EnhancedBrowserScreen(
    initialUrl: String = "https://www.google.com",
    onNavigateToTabs: () -> Unit = {},
    onNavigateToMenu: () -> Unit = {},
    onCreateNewTab: () -> Unit = {},
    modifier: Modifier = Modifier,
    webViewViewModel: WebViewViewModel = hiltViewModel(),
    tabViewModel: TabViewModel = hiltViewModel(),
    bookmarkViewModel: BookmarkViewModel = hiltViewModel(),
    historyViewModel: HistoryViewModel = hiltViewModel()
) {
    val webViewState by webViewViewModel.webViewState.collectAsState()
    val event by webViewViewModel.events.collectAsState()
    val tabState by tabViewModel.state.collectAsState()

    // Get bookmark state
    val isBookmarked by bookmarkViewModel.isBookmarked(webViewState.url).collectAsState(initial = false)

    // Load initial URL
    LaunchedEffect(Unit) {
        if (webViewState.url.isEmpty()) {
            webViewViewModel.loadUrl(initialUrl)
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Address Bar with URL Suggestions
        AddressBarWithSuggestions(
            url = webViewState.url,
            isLoading = webViewState.isLoading,
            canGoBack = webViewState.canGoBack,
            canGoForward = webViewState.canGoForward,
            tabCount = tabState.tabs.size,
            isBookmarked = isBookmarked,
            onUrlSubmit = { webViewViewModel.loadUrl(it) },
            onBackClick = { webViewViewModel.goBack() },
            onForwardClick = { webViewViewModel.goForward() },
            onRefreshClick = { webViewViewModel.reload() },
            onStopClick = { webViewViewModel.stopLoading() },
            onHomeClick = { webViewViewModel.loadUrl("https://www.google.com") },
            onTabsClick = onNavigateToTabs,
            onNewTabClick = { tabViewModel.createNewTab() },
            onBookmarkClick = {
                bookmarkViewModel.toggleBookmark(webViewState.title, webViewState.url)
            },
            onMenuClick = onNavigateToMenu
        )

        // Loading Progress Indicator
        PageLoadingIndicator(
            progress = webViewState.progress,
            isVisible = webViewState.isLoading
        )

        // WebView
        GlideWebView(
            url = webViewState.url,
            event = event,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            onUrlChange = { newUrl ->
                webViewViewModel.onUrlChanged(newUrl)
                // Update active tab's URL in database
                tabState.activeTab?.let { activeTab ->
                    tabViewModel.updateTabUrl(activeTab.id, newUrl)
                }
            },
            onTitleChange = { newTitle ->
                webViewViewModel.onTitleChanged(newTitle)
                // Update active tab's title in database
                tabState.activeTab?.let { activeTab ->
                    tabViewModel.updateTabTitle(activeTab.id, newTitle)
                }
                // Add to browsing history
                if (webViewState.url.isNotEmpty() && newTitle.isNotEmpty()) {
                    historyViewModel.addHistory(newTitle, webViewState.url)
                }
            },
            onProgressChange = { webViewViewModel.onProgressChanged(it) },
            onError = { webViewViewModel.onError(it) },
            onCanGoBackChange = { canBack ->
                webViewViewModel.onNavigationStateChanged(
                    canGoBack = canBack,
                    canGoForward = webViewState.canGoForward
                )
            },
            onCanGoForwardChange = { canForward ->
                webViewViewModel.onNavigationStateChanged(
                    canGoBack = webViewState.canGoBack,
                    canGoForward = canForward
                )
            },
            onEventHandled = { webViewViewModel.clearEvent() }
        )
    }
}