package com.inflexionco.glidebrowser.presentation.browser.navigation

import android.view.KeyEvent
import com.inflexionco.glidebrowser.domain.model.NavigationDirection
import com.inflexionco.glidebrowser.domain.model.NavigationMode
import com.inflexionco.glidebrowser.domain.navigation.WebNavigationManager
import com.inflexionco.glidebrowser.presentation.browser.bridge.JavaScriptInjector
import android.webkit.WebView
import timber.log.Timber
import javax.inject.Inject

/**
 * Handles D-pad key events for web page navigation
 */
class DPadNavigationHandler @Inject constructor(
    private val navigationManager: WebNavigationManager
) {

    companion object {
        // Scroll amount in pixels for scroll mode
        private const val SCROLL_AMOUNT_SMALL = 100
        private const val SCROLL_AMOUNT_LARGE = 300
    }

    /**
     * Handle D-pad key event
     * @return true if event was handled, false otherwise
     */
    fun handleKeyEvent(
        keyCode: Int,
        event: KeyEvent,
        webView: WebView,
        jsInjector: JavaScriptInjector
    ): Boolean {
        // Only handle key down events
        if (event.action != KeyEvent.ACTION_DOWN) {
            return false
        }

        return when (keyCode) {
            KeyEvent.KEYCODE_DPAD_UP -> {
                handleDirectionalKey(NavigationDirection.UP, webView, jsInjector)
                true
            }
            KeyEvent.KEYCODE_DPAD_DOWN -> {
                handleDirectionalKey(NavigationDirection.DOWN, webView, jsInjector)
                true
            }
            KeyEvent.KEYCODE_DPAD_LEFT -> {
                handleDirectionalKey(NavigationDirection.LEFT, webView, jsInjector)
                true
            }
            KeyEvent.KEYCODE_DPAD_RIGHT -> {
                handleDirectionalKey(NavigationDirection.RIGHT, webView, jsInjector)
                true
            }
            KeyEvent.KEYCODE_DPAD_CENTER, KeyEvent.KEYCODE_ENTER -> {
                handleCenterKey(webView, jsInjector)
                true
            }
            KeyEvent.KEYCODE_BACK -> {
                // Let the system handle back button
                false
            }
            KeyEvent.KEYCODE_MENU -> {
                // Toggle navigation mode
                navigationManager.toggleMode()
                true
            }
            else -> false
        }
    }

    /**
     * Handle directional D-pad keys (UP, DOWN, LEFT, RIGHT)
     */
    private fun handleDirectionalKey(
        direction: NavigationDirection,
        webView: WebView,
        jsInjector: JavaScriptInjector
    ) {
        val mode = navigationManager.getMode()

        when (mode) {
            NavigationMode.FOCUS -> handleFocusMode(direction, webView, jsInjector)
            NavigationMode.SCROLL -> handleScrollMode(direction, webView, jsInjector)
        }
    }

    /**
     * Handle navigation in Focus Mode
     */
    private fun handleFocusMode(
        direction: NavigationDirection,
        webView: WebView,
        jsInjector: JavaScriptInjector
    ) {
        val nextElement = navigationManager.navigate(direction)

        if (nextElement != null) {
            // Scroll element into view if needed
            if (navigationManager.shouldScrollToElement(nextElement)) {
                jsInjector.scrollToElement(webView, nextElement.id)
            }

            // Highlight the focused element (will be implemented with focus overlay)
            Timber.d("Focused element: ${nextElement.id} - ${nextElement.text.take(30)}")

            // TODO: Add visual focus indicator via JavaScript injection
            highlightElement(webView, jsInjector, nextElement.id)
        } else {
            // No element found, maybe switch to scroll mode or provide feedback
            Timber.d("No focusable element found in direction $direction")
        }
    }

    /**
     * Handle navigation in Scroll Mode
     */
    private fun handleScrollMode(
        direction: NavigationDirection,
        webView: WebView,
        jsInjector: JavaScriptInjector
    ) {
        val scrollAmount = SCROLL_AMOUNT_LARGE

        val (deltaX, deltaY) = when (direction) {
            NavigationDirection.UP -> Pair(0, -scrollAmount)
            NavigationDirection.DOWN -> Pair(0, scrollAmount)
            NavigationDirection.LEFT -> Pair(-scrollAmount, 0)
            NavigationDirection.RIGHT -> Pair(scrollAmount, 0)
        }

        jsInjector.smoothScrollBy(webView, deltaX, deltaY)
        Timber.d("Scrolled: deltaX=$deltaX, deltaY=$deltaY")
    }

    /**
     * Handle center/enter key press
     */
    private fun handleCenterKey(
        webView: WebView,
        jsInjector: JavaScriptInjector
    ) {
        val focusedElement = navigationManager.getFocusedElement()

        if (focusedElement != null && navigationManager.getMode() == NavigationMode.FOCUS) {
            // Click the focused element
            jsInjector.clickElement(webView, focusedElement.id)
            Timber.d("Clicked element: ${focusedElement.id}")
        } else {
            // No focused element, maybe trigger page action
            Timber.d("Center key pressed with no focused element")
        }
    }

    /**
     * Highlight the focused element (temporary implementation)
     * TODO: Replace with proper focus overlay system
     */
    private fun highlightElement(
        webView: WebView,
        jsInjector: JavaScriptInjector,
        elementId: String
    ) {
        val script = """
            (function() {
                // Remove previous highlight
                const prevHighlighted = document.querySelector('[data-glide-highlighted="true"]');
                if (prevHighlighted) {
                    prevHighlighted.style.outline = '';
                    prevHighlighted.removeAttribute('data-glide-highlighted');
                }

                // Add new highlight
                const element = document.querySelector('[data-glide-id="$elementId"]') ||
                               document.getElementById('$elementId');
                if (element) {
                    element.style.outline = '3px solid #1976D2';
                    element.setAttribute('data-glide-highlighted', 'true');
                }
            })();
        """.trimIndent()

        jsInjector.executeScript(webView, script)
    }

    /**
     * Remove all highlights
     */
    fun clearHighlights(webView: WebView, jsInjector: JavaScriptInjector) {
        val script = """
            (function() {
                const highlighted = document.querySelector('[data-glide-highlighted="true"]');
                if (highlighted) {
                    highlighted.style.outline = '';
                    highlighted.removeAttribute('data-glide-highlighted');
                }
            })();
        """.trimIndent()

        jsInjector.executeScript(webView, script)
    }
}