package com.inflexionco.glidebrowser.domain.navigation

import com.inflexionco.glidebrowser.domain.model.FocusableElement
import com.inflexionco.glidebrowser.domain.model.NavigationDirection
import com.inflexionco.glidebrowser.domain.model.NavigationMode
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import org.json.JSONArray
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Manages web page navigation state and interactions
 */
@Singleton
class WebNavigationManager @Inject constructor(
    private val spatialNavigation: SpatialNavigationEngine
) {

    private val _navigationState = MutableStateFlow(NavigationState())
    val navigationState: StateFlow<NavigationState> = _navigationState.asStateFlow()

    /**
     * Update detected elements from JavaScript bridge
     */
    fun updateElements(jsonArray: String) {
        try {
            val elements = parseElements(jsonArray)
            Timber.d("Parsed ${elements.size} focusable elements")

            _navigationState.value = _navigationState.value.copy(
                allElements = elements,
                // Clear focus if current element no longer exists
                focusedElement = _navigationState.value.focusedElement?.let { current ->
                    elements.firstOrNull { it.id == current.id }
                }
            )
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse elements")
        }
    }

    /**
     * Parse JSON array of elements into FocusableElement objects
     */
    private fun parseElements(jsonArray: String): List<FocusableElement> {
        val elements = mutableListOf<FocusableElement>()
        try {
            val array = JSONArray(jsonArray)
            for (i in 0 until array.length()) {
                val jsonObject = array.getJSONObject(i)
                FocusableElement.fromJson(jsonObject)?.let { elements.add(it) }
            }
        } catch (e: Exception) {
            Timber.e(e, "Failed to parse JSON elements")
        }
        return elements
    }

    /**
     * Navigate in the specified direction
     * @return The element to focus, or null if none found
     */
    fun navigate(direction: NavigationDirection): FocusableElement? {
        val state = _navigationState.value

        // Only navigate in Focus mode
        if (state.mode != NavigationMode.FOCUS) {
            return null
        }

        if (!state.hasElements) {
            Timber.w("No elements available for navigation")
            return null
        }

        val nextElement = spatialNavigation.findNextElement(
            currentElement = state.focusedElement,
            allElements = state.allElements,
            direction = direction,
            viewportWidth = state.viewportWidth,
            viewportHeight = state.viewportHeight,
            scrollX = state.scrollX.toFloat(),
            scrollY = state.scrollY.toFloat()
        )

        if (nextElement != null) {
            _navigationState.value = state.copy(focusedElement = nextElement)
            Timber.d("Navigated $direction to element: ${nextElement.id} (${nextElement.text.take(30)})")
        } else {
            Timber.d("No element found in direction $direction")
        }

        return nextElement
    }

    /**
     * Toggle between Focus and Scroll modes
     */
    fun toggleMode() {
        val currentMode = _navigationState.value.mode
        val newMode = when (currentMode) {
            NavigationMode.FOCUS -> NavigationMode.SCROLL
            NavigationMode.SCROLL -> NavigationMode.FOCUS
        }
        _navigationState.value = _navigationState.value.copy(mode = newMode)
        Timber.d("Navigation mode changed to: $newMode")
    }

    /**
     * Set navigation mode explicitly
     */
    fun setMode(mode: NavigationMode) {
        _navigationState.value = _navigationState.value.copy(mode = mode)
        Timber.d("Navigation mode set to: $mode")
    }

    /**
     * Get current navigation mode
     */
    fun getMode(): NavigationMode = _navigationState.value.mode

    /**
     * Update viewport dimensions
     */
    fun updateViewport(width: Float, height: Float) {
        _navigationState.value = _navigationState.value.copy(
            viewportWidth = width,
            viewportHeight = height
        )
    }

    /**
     * Update scroll position
     */
    fun updateScrollPosition(x: Int, y: Int) {
        _navigationState.value = _navigationState.value.copy(
            scrollX = x,
            scrollY = y
        )
    }

    /**
     * Check if element should be scrolled into view
     */
    fun shouldScrollToElement(element: FocusableElement): Boolean {
        val state = _navigationState.value
        return spatialNavigation.shouldScrollToElement(
            element = element,
            viewportWidth = state.viewportWidth,
            viewportHeight = state.viewportHeight,
            scrollX = state.scrollX.toFloat(),
            scrollY = state.scrollY.toFloat()
        )
    }

    /**
     * Calculate scroll position to center element
     */
    fun calculateScrollPosition(element: FocusableElement): Pair<Float, Float> {
        val state = _navigationState.value
        return spatialNavigation.calculateScrollOffset(
            element = element,
            viewportWidth = state.viewportWidth,
            viewportHeight = state.viewportHeight
        )
    }

    /**
     * Get currently focused element
     */
    fun getFocusedElement(): FocusableElement? = _navigationState.value.focusedElement

    /**
     * Clear focused element
     */
    fun clearFocus() {
        _navigationState.value = _navigationState.value.copy(focusedElement = null)
        Timber.d("Focus cleared")
    }

    /**
     * Reset navigation state (e.g., on page load)
     */
    fun reset() {
        _navigationState.value = NavigationState(
            viewportWidth = _navigationState.value.viewportWidth,
            viewportHeight = _navigationState.value.viewportHeight
        )
        Timber.d("Navigation state reset")
    }

    /**
     * Set loading state
     */
    fun setLoading(isLoading: Boolean) {
        _navigationState.value = _navigationState.value.copy(isLoading = isLoading)
    }

    /**
     * Focus specific element by ID
     */
    fun focusElementById(elementId: String): FocusableElement? {
        val element = _navigationState.value.allElements.firstOrNull { it.id == elementId }
        if (element != null) {
            _navigationState.value = _navigationState.value.copy(focusedElement = element)
            Timber.d("Focused element: $elementId")
        }
        return element
    }
}