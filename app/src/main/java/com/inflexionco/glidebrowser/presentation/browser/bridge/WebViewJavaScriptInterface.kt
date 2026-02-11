package com.inflexionco.glidebrowser.presentation.browser.bridge

import android.webkit.JavascriptInterface
import timber.log.Timber

/**
 * JavaScript interface for WebView bridge communication
 * Allows JavaScript code to call Kotlin methods
 */
class WebViewJavaScriptInterface(
    private val callback: Callback
) {

    interface Callback {
        fun onElementsDetected(jsonArray: String)
        fun onElementClicked(elementId: String)
        fun onConsoleLog(message: String)
        fun onPageScrolled(scrollX: Int, scrollY: Int)
    }

    @JavascriptInterface
    fun postElementsData(jsonArray: String) {
        Timber.d("Elements data received: ${jsonArray.take(200)}...")
        callback.onElementsDetected(jsonArray)
    }

    @JavascriptInterface
    fun postElementClicked(elementId: String) {
        Timber.d("Element clicked: $elementId")
        callback.onElementClicked(elementId)
    }

    @JavascriptInterface
    fun postConsoleLog(message: String) {
        Timber.d("WebView Console: $message")
        callback.onConsoleLog(message)
    }

    @JavascriptInterface
    fun postPageScrolled(scrollX: Int, scrollY: Int) {
        callback.onPageScrolled(scrollX, scrollY)
    }

    companion object {
        const val INTERFACE_NAME = "GlideAndroid"
    }
}