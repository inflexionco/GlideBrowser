package com.inflexionco.glidebrowser.presentation.browser.components

import android.graphics.Bitmap
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewState

@Composable
fun GlideWebView(
    url: String,
    modifier: Modifier = Modifier,
    onUrlChange: (String) -> Unit = {},
    onTitleChange: (String) -> Unit = {},
    onProgressChange: (Int) -> Unit = {},
    onError: (String) -> Unit = {},
    onCanGoBackChange: (Boolean) -> Unit = {},
    onCanGoForwardChange: (Boolean) -> Unit = {}
) {
    val state = rememberWebViewState(url = url)

    WebView(
        state = state,
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
}