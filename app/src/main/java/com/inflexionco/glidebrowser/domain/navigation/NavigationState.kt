package com.inflexionco.glidebrowser.domain.navigation

import com.inflexionco.glidebrowser.domain.model.FocusableElement
import com.inflexionco.glidebrowser.domain.model.NavigationMode

/**
 * Represents the current state of web page navigation
 */
data class NavigationState(
    val mode: NavigationMode = NavigationMode.FOCUS,
    val focusedElement: FocusableElement? = null,
    val allElements: List<FocusableElement> = emptyList(),
    val scrollX: Int = 0,
    val scrollY: Int = 0,
    val viewportWidth: Float = 0f,
    val viewportHeight: Float = 0f,
    val isLoading: Boolean = false
) {
    /**
     * Check if there are any focusable elements available
     */
    val hasElements: Boolean
        get() = allElements.isNotEmpty()

    /**
     * Check if an element is currently focused
     */
    val hasFocus: Boolean
        get() = focusedElement != null

    /**
     * Get the index of the currently focused element
     */
    val focusedElementIndex: Int
        get() = if (focusedElement != null) {
            allElements.indexOfFirst { it.id == focusedElement.id }
        } else {
            -1
        }

    /**
     * Get total number of focusable elements
     */
    val elementCount: Int
        get() = allElements.size
}