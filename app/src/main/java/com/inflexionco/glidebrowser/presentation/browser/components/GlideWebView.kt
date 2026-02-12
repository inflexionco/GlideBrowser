package com.inflexionco.glidebrowser.presentation.browser.components

import android.graphics.Bitmap
import android.net.http.SslError
import android.webkit.SslErrorHandler
import android.webkit.WebChromeClient
import android.webkit.WebResourceError
import android.webkit.WebResourceRequest
import android.webkit.WebView
import android.webkit.WebViewClient
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.accompanist.web.AccompanistWebViewClient
import com.google.accompanist.web.WebView
import com.google.accompanist.web.rememberWebViewNavigator
import com.google.accompanist.web.rememberWebViewState
import com.inflexionco.glidebrowser.domain.model.SslErrorInfo
import com.inflexionco.glidebrowser.domain.model.SslErrorType
import com.inflexionco.glidebrowser.domain.model.WebViewEvent
import com.inflexionco.glidebrowser.presentation.browser.bridge.JavaScriptInjector
import com.inflexionco.glidebrowser.presentation.browser.bridge.WebViewJavaScriptInterface
import timber.log.Timber
import java.text.SimpleDateFormat
import java.util.Locale

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
    onEventHandled: () -> Unit = {},
    onElementsDetected: (String) -> Unit = {},
    onConsoleLog: (String) -> Unit = {},
    onPageScrolled: (Int, Int) -> Unit = { _, _ -> },
    onWebViewCreated: (WebView, JavaScriptInjector) -> Unit = { _, _ -> },
    onSslError: (SslErrorInfo, SslErrorHandler) -> Unit = { _, handler -> handler.cancel() }
) {
    val context = LocalContext.current
    val state = rememberWebViewState(url = url)
    val navigator = rememberWebViewNavigator()

    val jsInjector = remember { JavaScriptInjector(context) }

    val jsInterface = remember {
        WebViewJavaScriptInterface(object : WebViewJavaScriptInterface.Callback {
            override fun onElementsDetected(jsonArray: String) {
                Timber.d("Elements detected: ${jsonArray.take(200)}...")
                onElementsDetected(jsonArray)
            }

            override fun onElementClicked(elementId: String) {
                Timber.d("Element clicked: $elementId")
            }

            override fun onConsoleLog(message: String) {
                Timber.d("WebView Console: $message")
                onConsoleLog(message)
            }

            override fun onPageScrolled(scrollX: Int, scrollY: Int) {
                onPageScrolled(scrollX, scrollY)
            }
        })
    }

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
            configureWebView(webView, jsInterface)
            webView.webChromeClient = object : WebChromeClient() {
                override fun onProgressChanged(view: WebView?, newProgress: Int) {
                    onProgressChange(newProgress)
                }

                override fun onReceivedTitle(view: WebView?, title: String?) {
                    title?.let { onTitleChange(it) }
                }
            }

            // Notify that WebView is ready
            onWebViewCreated(webView, jsInjector)
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

                // Inject navigation script and trigger element detection
                jsInjector.injectNavigationScript(view)
                view.postDelayed({
                    jsInjector.detectElements(view)
                }, 500)
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

            override fun onReceivedSslError(
                view: WebView,
                handler: SslErrorHandler,
                error: SslError
            ) {
                Timber.w("SSL Error occurred: ${error.primaryError}")
                val sslErrorInfo = parseSslError(error, view.url ?: "")
                onSslError(sslErrorInfo, handler)
            }
        }
    )
}

private fun parseSslError(sslError: SslError, url: String): SslErrorInfo {
    val errorType = when (sslError.primaryError) {
        SslError.SSL_NOTYETVALID -> SslErrorType.NOT_YET_VALID
        SslError.SSL_EXPIRED -> SslErrorType.EXPIRED
        SslError.SSL_IDMISMATCH -> SslErrorType.ID_MISMATCH
        SslError.SSL_UNTRUSTED -> SslErrorType.UNTRUSTED
        SslError.SSL_DATE_INVALID -> SslErrorType.DATE_INVALID
        SslError.SSL_INVALID -> SslErrorType.INVALID
        else -> SslErrorType.UNKNOWN
    }

    val certificate = sslError.certificate
    val dateFormat = SimpleDateFormat("MMM dd, yyyy", Locale.getDefault())

    return SslErrorInfo(
        url = url,
        errorType = errorType,
        certificateIssuer = certificate.issuedBy?.dName,
        certificateSubject = certificate.issuedTo?.dName,
        certificateValidFrom = certificate.validNotBeforeDate?.let { dateFormat.format(it) },
        certificateValidTo = certificate.validNotAfterDate?.let { dateFormat.format(it) }
    )
}

private fun configureWebView(webView: WebView, jsInterface: WebViewJavaScriptInterface) {
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

    // Add JavaScript interface for bridge communication
    webView.addJavascriptInterface(jsInterface, WebViewJavaScriptInterface.INTERFACE_NAME)
    Timber.d("JavaScript interface '${WebViewJavaScriptInterface.INTERFACE_NAME}' added to WebView")

    // Disable focus for TV - allows D-pad navigation to reach buttons
    webView.isFocusable = false
    webView.isFocusableInTouchMode = false
}