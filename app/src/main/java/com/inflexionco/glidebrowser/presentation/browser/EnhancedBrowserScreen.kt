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
import com.inflexionco.glidebrowser.presentation.browser.components.AddressBar
import com.inflexionco.glidebrowser.presentation.browser.components.GlideWebView
import com.inflexionco.glidebrowser.presentation.browser.components.PageLoadingIndicator

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun EnhancedBrowserScreen(
    initialUrl: String = "https://www.google.com",
    onNavigateToTabs: () -> Unit = {},
    modifier: Modifier = Modifier,
    webViewViewModel: WebViewViewModel = hiltViewModel()
) {
    val webViewState by webViewViewModel.webViewState.collectAsState()
    val event by webViewViewModel.events.collectAsState()

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
        // Address Bar
        AddressBar(
            url = webViewState.url,
            isLoading = webViewState.isLoading,
            canGoBack = webViewState.canGoBack,
            canGoForward = webViewState.canGoForward,
            onUrlSubmit = { webViewViewModel.loadUrl(it) },
            onBackClick = { webViewViewModel.goBack() },
            onForwardClick = { webViewViewModel.goForward() },
            onRefreshClick = { webViewViewModel.reload() },
            onStopClick = { webViewViewModel.stopLoading() },
            onHomeClick = { webViewViewModel.loadUrl("https://www.google.com") },
            onTabsClick = onNavigateToTabs
        )

        // Loading Progress Indicator
        PageLoadingIndicator(
            progress = webViewState.progress,
            isVisible = webViewState.isLoading
        )

        // WebView
        GlideWebView(
            url = webViewState.url,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            onUrlChange = { webViewViewModel.onUrlChanged(it) },
            onTitleChange = { webViewViewModel.onTitleChanged(it) },
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
            }
        )
    }

    // Clear event after handling
    LaunchedEffect(event) {
        if (event != null) {
            webViewViewModel.clearEvent()
        }
    }
}