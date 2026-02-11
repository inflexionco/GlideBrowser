package com.inflexionco.glidebrowser.presentation.browser.bridge

import android.content.Context
import android.webkit.WebView
import timber.log.Timber
import java.io.BufferedReader
import java.io.InputStreamReader

/**
 * Manages JavaScript injection into WebView
 */
class JavaScriptInjector(private val context: Context) {

    private var navigationScript: String? = null

    /**
     * Load navigation JavaScript from resources
     */
    private fun loadNavigationScript(): String {
        if (navigationScript == null) {
            try {
                val inputStream = context.resources.openRawResource(
                    context.resources.getIdentifier(
                        "navigation_inject",
                        "raw",
                        context.packageName
                    )
                )
                val reader = BufferedReader(InputStreamReader(inputStream))
                navigationScript = reader.use { it.readText() }
                Timber.d("Navigation script loaded: ${navigationScript?.length} characters")
            } catch (e: Exception) {
                Timber.e(e, "Failed to load navigation script")
                navigationScript = ""
            }
        }
        return navigationScript ?: ""
    }

    /**
     * Inject navigation JavaScript into WebView
     */
    fun injectNavigationScript(webView: WebView) {
        val script = loadNavigationScript()
        if (script.isNotEmpty()) {
            webView.evaluateJavascript(script) { result ->
                Timber.d("Navigation script injected, result: $result")
            }
        }
    }

    /**
     * Trigger element detection
     */
    fun detectElements(webView: WebView) {
        webView.evaluateJavascript("window.GlideBrowser.detectElements();") { result ->
            Timber.d("Element detection triggered: $result")
        }
    }

    /**
     * Click element by ID
     */
    fun clickElement(webView: WebView, elementId: String) {
        val script = "window.GlideBrowser.clickElement('$elementId');"
        webView.evaluateJavascript(script) { result ->
            Timber.d("Click element result: $result")
        }
    }

    /**
     * Scroll to element
     */
    fun scrollToElement(webView: WebView, elementId: String) {
        val script = "window.GlideBrowser.scrollToElement('$elementId');"
        webView.evaluateJavascript(script) { result ->
            Timber.d("Scroll to element result: $result")
        }
    }

    /**
     * Execute custom JavaScript
     */
    fun executeScript(webView: WebView, script: String, callback: ((String?) -> Unit)? = null) {
        webView.evaluateJavascript(script) { result ->
            Timber.d("Script executed: $script, result: $result")
            callback?.invoke(result)
        }
    }

    /**
     * Get page scroll position
     */
    fun getScrollPosition(webView: WebView, callback: (Int, Int) -> Unit) {
        webView.evaluateJavascript(
            "(function() { return JSON.stringify({x: window.scrollX, y: window.scrollY}); })();"
        ) { result ->
            try {
                // Parse JSON result
                val cleaned = result.replace("\\", "").trim('"')
                val parts = cleaned.substringAfter("{").substringBefore("}").split(",")
                val x = parts[0].substringAfter(":").toInt()
                val y = parts[1].substringAfter(":").toInt()
                callback(x, y)
            } catch (e: Exception) {
                Timber.e(e, "Failed to parse scroll position")
                callback(0, 0)
            }
        }
    }

    /**
     * Smooth scroll page
     */
    fun smoothScrollBy(webView: WebView, deltaX: Int, deltaY: Int) {
        val script = "window.scrollBy({ left: $deltaX, top: $deltaY, behavior: 'smooth' });"
        webView.evaluateJavascript(script, null)
    }
}