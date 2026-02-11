package com.inflexionco.glidebrowser.domain.model

/**
 * Navigation modes for D-pad interaction
 */
enum class NavigationMode {
    /**
     * Focus Mode: Navigate between focusable elements (links, buttons, inputs)
     */
    FOCUS,

    /**
     * Scroll Mode: Scroll the page content
     */
    SCROLL
}