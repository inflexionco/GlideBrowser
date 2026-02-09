package com.inflexionco.glidebrowser.presentation.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inflexionco.glidebrowser.domain.model.WebViewEvent
import com.inflexionco.glidebrowser.domain.model.WebViewState
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class WebViewViewModel @Inject constructor() : ViewModel() {

    private val _webViewState = MutableStateFlow(WebViewState())
    val webViewState: StateFlow<WebViewState> = _webViewState.asStateFlow()

    private val _events = MutableStateFlow<WebViewEvent?>(null)
    val events: StateFlow<WebViewEvent?> = _events.asStateFlow()

    init {
        Timber.d("WebViewViewModel initialized")
    }

    fun loadUrl(url: String) {
        val formattedUrl = formatUrl(url)
        Timber.d("Loading URL: $formattedUrl")
        _events.value = WebViewEvent.LoadUrl(formattedUrl)
        _webViewState.update { it.copy(url = formattedUrl, isLoading = true, error = null) }
    }

    fun onUrlChanged(url: String) {
        Timber.d("URL changed: $url")
        _webViewState.update { it.copy(url = url) }
    }

    fun onTitleChanged(title: String) {
        Timber.d("Title changed: $title")
        _webViewState.update { it.copy(title = title) }
    }

    fun onProgressChanged(progress: Int) {
        _webViewState.update {
            it.copy(
                progress = progress,
                isLoading = progress < 100
            )
        }
    }

    fun onNavigationStateChanged(canGoBack: Boolean, canGoForward: Boolean) {
        _webViewState.update {
            it.copy(
                canGoBack = canGoBack,
                canGoForward = canGoForward
            )
        }
    }

    fun onError(error: String) {
        Timber.e("WebView error: $error")
        _webViewState.update {
            it.copy(
                error = error,
                isLoading = false
            )
        }
    }

    fun goBack() {
        if (_webViewState.value.canGoBack) {
            Timber.d("Going back")
            _events.value = WebViewEvent.GoBack
        }
    }

    fun goForward() {
        if (_webViewState.value.canGoForward) {
            Timber.d("Going forward")
            _events.value = WebViewEvent.GoForward
        }
    }

    fun reload() {
        Timber.d("Reloading page")
        _events.value = WebViewEvent.Reload
    }

    fun stopLoading() {
        Timber.d("Stopping page load")
        _events.value = WebViewEvent.Stop
        _webViewState.update { it.copy(isLoading = false) }
    }

    fun clearHistory() {
        Timber.d("Clearing history")
        _events.value = WebViewEvent.ClearHistory
    }

    fun zoomIn() {
        viewModelScope.launch {
            val newZoom = (_webViewState.value.zoomLevel * 1.2f).coerceAtMost(3.0f)
            _webViewState.update { it.copy(zoomLevel = newZoom) }
            _events.value = WebViewEvent.ZoomIn()
            Timber.d("Zoom in: $newZoom")
        }
    }

    fun zoomOut() {
        viewModelScope.launch {
            val newZoom = (_webViewState.value.zoomLevel * 0.8f).coerceAtLeast(0.5f)
            _webViewState.update { it.copy(zoomLevel = newZoom) }
            _events.value = WebViewEvent.ZoomOut()
            Timber.d("Zoom out: $newZoom")
        }
    }

    fun resetZoom() {
        viewModelScope.launch {
            _webViewState.update { it.copy(zoomLevel = 1.0f) }
            _events.value = WebViewEvent.ResetZoom
            Timber.d("Reset zoom")
        }
    }

    fun clearEvent() {
        _events.value = null
    }

    /**
     * Format URL by adding https:// if no protocol is specified
     */
    private fun formatUrl(url: String): String {
        return when {
            url.startsWith("http://") || url.startsWith("https://") -> url
            url.startsWith("file://") || url.startsWith("data:") -> url
            url.contains(".") -> "https://$url"
            else -> "https://www.google.com/search?q=${url.replace(" ", "+")}"
        }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("WebViewViewModel cleared")
    }
}