package com.inflexionco.glidebrowser.domain.repository

import com.inflexionco.glidebrowser.domain.model.NewTabBehavior
import com.inflexionco.glidebrowser.domain.model.SearchEngine
import com.inflexionco.glidebrowser.domain.model.UserPreferences
import kotlinx.coroutines.flow.Flow

interface SettingsRepository {
    // Get all preferences as Flow
    fun getUserPreferences(): Flow<UserPreferences>

    // General settings
    suspend fun setHomepage(url: String)
    suspend fun setSearchEngine(searchEngine: SearchEngine)
    suspend fun setNewTabBehavior(behavior: NewTabBehavior)

    // Privacy settings
    suspend fun setEnableCookies(enabled: Boolean)
    suspend fun setEnableThirdPartyCookies(enabled: Boolean)
    suspend fun setClearHistoryOnExit(enabled: Boolean)
    suspend fun setClearCacheOnExit(enabled: Boolean)

    // Appearance settings
    suspend fun setTextScale(scale: Float)
    suspend fun setForceZoomEnabled(enabled: Boolean)

    // Display settings
    suspend fun setDesktopMode(enabled: Boolean)

    // Advanced settings
    suspend fun setEnableJavaScript(enabled: Boolean)
    suspend fun setEnableDomStorage(enabled: Boolean)
    suspend fun setEnablePopupBlocking(enabled: Boolean)
    suspend fun setEnableHardwareAcceleration(enabled: Boolean)
    suspend fun setUserAgentString(userAgent: String)

    // Reset to defaults
    suspend fun resetToDefaults()
}