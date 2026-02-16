package com.inflexionco.glidebrowser.domain.model

/**
 * User preferences for browser settings
 */
data class UserPreferences(
    // General
    val homepage: String = "https://www.google.com",
    val searchEngine: SearchEngine = SearchEngine.GOOGLE,
    val openNewTabBehavior: NewTabBehavior = NewTabBehavior.HOMEPAGE,

    // Privacy
    val enableCookies: Boolean = true,
    val enableThirdPartyCookies: Boolean = false,
    val clearHistoryOnExit: Boolean = false,
    val clearCacheOnExit: Boolean = false,

    // Appearance
    val textScale: Float = 1.0f, // 0.8 to 1.5
    val forceZoomEnabled: Boolean = true,

    // Display
    val desktopMode: Boolean = true, // Request desktop sites by default on TV

    // Advanced
    val enableJavaScript: Boolean = true,
    val enableDomStorage: Boolean = true,
    val enablePopupBlocking: Boolean = true,
    val enableHardwareAcceleration: Boolean = true,
    val userAgentString: String = "" // Empty = default
)

enum class SearchEngine(val displayName: String, val searchUrl: String) {
    GOOGLE("Google", "https://www.google.com/search?q=%s"),
    BING("Bing", "https://www.bing.com/search?q=%s"),
    DUCKDUCKGO("DuckDuckGo", "https://duckduckgo.com/?q=%s"),
    YAHOO("Yahoo", "https://search.yahoo.com/search?p=%s")
}

enum class NewTabBehavior(val displayName: String) {
    HOMEPAGE("Homepage"),
    BLANK("Blank Page"),
    CONTINUE_WHERE_LEFT("Continue Where I Left Off")
}