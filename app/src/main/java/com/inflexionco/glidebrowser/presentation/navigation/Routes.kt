package com.inflexionco.glidebrowser.presentation.navigation

import kotlinx.serialization.Serializable

@Serializable
sealed class Routes {

    @Serializable
    data object Home : Routes()

    @Serializable
    data class Browser(val url: String? = null) : Routes()

    @Serializable
    data object TabSwitcher : Routes()

    @Serializable
    data object Settings : Routes()

    @Serializable
    data object Bookmarks : Routes()

    @Serializable
    data object History : Routes()

    @Serializable
    data object Downloads : Routes()
}