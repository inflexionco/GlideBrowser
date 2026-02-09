package com.inflexionco.glidebrowser.domain.model

/**
 * Represents the state of a WebView instance
 */
data class WebViewState(
    val url: String = "",
    val title: String = "",
    val favicon: String? = null,
    val progress: Int = 0,
    val isLoading: Boolean = false,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val error: String? = null,
    val zoomLevel: Float = 1.0f
)

/**
 * Sealed class representing WebView events
 */
sealed class WebViewEvent {
    data class LoadUrl(val url: String) : WebViewEvent()
    object GoBack : WebViewEvent()
    object GoForward : WebViewEvent()
    object Reload : WebViewEvent()
    object Stop : WebViewEvent()
    object ClearHistory : WebViewEvent()
    data class ZoomIn(val factor: Float = 1.2f) : WebViewEvent()
    data class ZoomOut(val factor: Float = 0.8f) : WebViewEvent()
    object ResetZoom : WebViewEvent()
}