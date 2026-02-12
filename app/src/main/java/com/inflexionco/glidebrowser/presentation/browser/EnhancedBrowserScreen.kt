package com.inflexionco.glidebrowser.presentation.browser

import android.view.KeyEvent
import android.webkit.WebView
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.compose.ui.platform.LocalConfiguration
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.dp
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.inflexionco.glidebrowser.domain.model.WebViewEvent
import com.inflexionco.glidebrowser.domain.navigation.WebNavigationManager
import com.inflexionco.glidebrowser.presentation.browser.bridge.JavaScriptInjector
import com.inflexionco.glidebrowser.presentation.browser.components.AddressBarWithSuggestions
import com.inflexionco.glidebrowser.presentation.browser.components.GlideWebView
import com.inflexionco.glidebrowser.presentation.browser.components.NavigationModeIndicator
import com.inflexionco.glidebrowser.presentation.browser.components.PageLoadingIndicator
import com.inflexionco.glidebrowser.presentation.browser.navigation.DPadNavigationHandler
import com.inflexionco.glidebrowser.presentation.tabs.TabViewModel
import dagger.hilt.android.EntryPointAccessors
import dagger.hilt.EntryPoint
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent

@EntryPoint
@InstallIn(SingletonComponent::class)
interface NavigationEntryPoint {
    fun navigationManager(): WebNavigationManager
    fun dpadHandler(): DPadNavigationHandler
}

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
    val context = LocalContext.current
    val navigationEntryPoint = remember {
        EntryPointAccessors.fromApplication(
            context.applicationContext,
            NavigationEntryPoint::class.java
        )
    }
    val navigationManager = remember { navigationEntryPoint.navigationManager() }
    val dpadHandler = remember { navigationEntryPoint.dpadHandler() }
    val webViewState by webViewViewModel.webViewState.collectAsState()
    val event by webViewViewModel.events.collectAsState()
    val tabState by tabViewModel.state.collectAsState()
    val navigationState by navigationManager.navigationState.collectAsState()

    // Get bookmark state
    val isBookmarked by bookmarkViewModel.isBookmarked(webViewState.url).collectAsState(initial = false)

    // WebView and JSInjector references
    var webView by remember { mutableStateOf<WebView?>(null) }
    var jsInjector by remember { mutableStateOf<JavaScriptInjector?>(null) }

    // Mode indicator visibility timer
    var showModeIndicator by remember { mutableStateOf(false) }

    // Get screen dimensions for viewport
    val configuration = LocalConfiguration.current
    val density = LocalDensity.current
    val screenWidthPx = with(density) { configuration.screenWidthDp.dp.toPx() }
    val screenHeightPx = with(density) { configuration.screenHeightDp.dp.toPx() }

    // Update viewport dimensions
    LaunchedEffect(screenWidthPx, screenHeightPx) {
        navigationManager.updateViewport(screenWidthPx, screenHeightPx)
    }

    // Load initial URL
    LaunchedEffect(Unit) {
        if (webViewState.url.isEmpty()) {
            webViewViewModel.loadUrl(initialUrl)
        }
    }

    // Show mode indicator when mode changes
    LaunchedEffect(navigationState.mode) {
        showModeIndicator = true
        kotlinx.coroutines.delay(2000) // Show for 2 seconds
        showModeIndicator = false
    }

    // Reset navigation on URL change
    LaunchedEffect(webViewState.url) {
        navigationManager.reset()
    }

    // Cleanup on dispose
    DisposableEffect(Unit) {
        onDispose {
            webView?.let { wv ->
                jsInjector?.let { ji ->
                    dpadHandler.clearHighlights(wv, ji)
                }
            }
        }
    }

    Box(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .onPreviewKeyEvent { keyEvent ->
                // Handle D-pad navigation
                webView?.let { wv ->
                    jsInjector?.let { ji ->
                        dpadHandler.handleKeyEvent(
                            keyCode = keyEvent.nativeKeyEvent.keyCode,
                            event = keyEvent.nativeKeyEvent,
                            webView = wv,
                            jsInjector = ji
                        )
                    }
                } ?: false
            }
    ) {
        Column(
            modifier = Modifier.fillMaxSize()
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
                onEventHandled = { webViewViewModel.clearEvent() },
                onElementsDetected = { jsonArray ->
                    navigationManager.updateElements(jsonArray)
                },
                onPageScrolled = { x, y ->
                    navigationManager.updateScrollPosition(x, y)
                },
                onWebViewCreated = { wv, ji ->
                    webView = wv
                    jsInjector = ji
                }
            )
        }

        // Navigation Mode Indicator (overlay)
        NavigationModeIndicator(
            mode = navigationState.mode,
            isVisible = showModeIndicator,
            elementCount = navigationState.elementCount,
            focusedIndex = navigationState.focusedElementIndex,
            modifier = Modifier
                .align(Alignment.BottomEnd)
                .padding(16.dp)
        )
    }
}