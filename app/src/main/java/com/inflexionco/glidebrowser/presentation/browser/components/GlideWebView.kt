package com.inflexionco.glidebrowser.presentation.browser.components

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.inflexionco.glidebrowser.domain.model.WebViewEvent

@Composable
fun GlideWebView(
    url: String,
    modifier: Modifier = Modifier,
    event: WebViewEvent? = null,
    onUrlChange: (String) -> Unit = {},
    onTitleChange: (String) -> Unit = {},
    onProgressChange: (Int) -> Unit = {},
    onError: (String) -> Unit = {},
    onCanGoBackChange: (Boolean) -> Unit = {},
    onCanGoForwardChange: (Boolean) -> Unit = {},
    onEventHandled: () -> Unit = {}
) {
    val state = rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()

    // Handle WebView events
    LaunchedEffect(event) {
        when (event) {
            is WebViewEvent.LoadUrl -> navigator.loadUrl(event.url)
            is WebViewEvent.GoBack -> navigator.navigateBack()
            is WebViewEvent.GoForward -> navigator.navigateForward()
            is WebViewEvent.Reload -> navigator.reload()
            is WebViewEvent.Stop -> navigator.stopLoading()
            else -> {}
        }
        if (event != null) {
            onEventHandled()
        }
    }

    WebView(
        state = state,
        navigator = navigator,
        modifier = modifier,
        onCreated = { webView ->
            configureWebView(webView)
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    onProgressChange(newProgress)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    title?.let { onTitleChange(it) }
                }
            }
        },
        client = object : AccompanistWebViewClient() {
            override fun onPageStarted(view: WebView, url: String?, favicon: Bitmap?) {
                super.onPageStarted(view, url, favicon)
                url?.let { onUrlChange(it) }
                onProgressChange(0)
            }

            override fun onPageFinished(view: WebView, url: String?) {
                super.onPageFinished(view, url)
                onCanGoBackChange(view.canGoBack())
                onCanGoForwardChange(view.canGoForward())
                onProgressChange(100)
            }

            override fun onReceivedError(
                view: WebView,
                request: WebResourceRequest?,
                error: WebResourceError?
            ) {
                super.onReceivedError(view, request, error)
                error?.description?.let {
                    onError(it.toString())
                }
            }
        }
    )
}

private fun configureWebView(webView: WebView) {
    webView.settings.apply {
        javaScriptEnabled = true
        domStorageEnabled = true
        databaseEnabled = true
        setSupportZoom(true)
        builtInZoomControls = true
        displayZoomControls = false
        loadWithOverviewMode = true
        useWideViewPort = true
        mediaPlaybackRequiresUserGesture = false
    }

    // Enable hardware acceleration
    webView.setLayerType(WebView.LAYER_TYPE_HARDWARE, null)

    // Disable focus for TV - allows D-pad navigation to reach buttons
    webView.isFocusable = false
    webView.isFocusableInTouchMode = false
}