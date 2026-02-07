package com.inflexionco.glidebrowser.presentation.browser

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.compose.material3.LinearProgressIndicator
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.presentation.browser.components.GlideWebView

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BrowserScreen(
    initialUrl: String?,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BrowserViewModel = hiltViewModel()
) {
    val uiState by viewModel.uiState.collectAsState()

    val urlToLoad = initialUrl ?: "https://www.google.com"

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
    ) {
        // Address bar and controls
        BrowserToolbar(
            url = uiState.currentUrl.ifEmpty { urlToLoad },
            title = uiState.pageTitle,
            canGoBack = uiState.canGoBack,
            canGoForward = uiState.canGoForward,
            onNavigateBack = onNavigateBack
        )

        // Loading indicator
        if (uiState.isLoading) {
            LinearProgressIndicator(
                progress = { uiState.loadingProgress / 100f },
                modifier = Modifier.fillMaxWidth()
            )
        } else {
            Spacer(modifier = Modifier.height(4.dp))
        }

        // Error message
        if (uiState.hasError && uiState.errorMessage != null) {
            Box(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(MaterialTheme.colorScheme.errorContainer)
                    .padding(16.dp)
            ) {
                Text(
                    text = "Error: ${uiState.errorMessage}",
                    color = MaterialTheme.colorScheme.onErrorContainer
                )
            }
        }

        // WebView
        GlideWebView(
            url = urlToLoad,
            modifier = Modifier
                .fillMaxSize()
                .weight(1f),
            onUrlChange = { viewModel.updateUrl(it) },
            onTitleChange = { viewModel.updateTitle(it) },
            onProgressChange = { viewModel.updateLoadingProgress(it) },
            onError = { viewModel.onError(it) },
            onCanGoBackChange = { viewModel.canGoBack(it) },
            onCanGoForwardChange = { viewModel.canGoForward(it) }
        )
    }
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
private fun BrowserToolbar(
    url: String,
    title: String,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier
) {
    Column(
        modifier = modifier
            .fillMaxWidth()
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = 24.dp, vertical = 16.dp)
    ) {
        // Title
        if (title.isNotEmpty()) {
            Text(
                text = title,
                style = MaterialTheme.typography.titleMedium,
                color = MaterialTheme.colorScheme.onSurface
            )
            Spacer(modifier = Modifier.height(4.dp))
        }

        // URL
        Text(
            text = url,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant
        )
    }
}