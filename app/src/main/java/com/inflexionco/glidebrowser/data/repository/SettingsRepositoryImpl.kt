package com.inflexionco.glidebrowser.data.repository

import android.content.Context
import androidx.datastore.core.DataStore
import androidx.datastore.preferences.core.Preferences
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.floatPreferencesKey
import androidx.datastore.preferences.core.stringPreferencesKey
import androidx.datastore.preferences.preferencesDataStore
import com.inflexionco.glidebrowser.domain.model.NewTabBehavior
import com.inflexionco.glidebrowser.domain.model.SearchEngine
import com.inflexionco.glidebrowser.domain.model.UserPreferences
import com.inflexionco.glidebrowser.domain.repository.SettingsRepository
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

private val Context.dataStore: DataStore<Preferences> by preferencesDataStore(name = "settings")

@Singleton
class SettingsRepositoryImpl @Inject constructor(
    @ApplicationContext private val context: Context
) : SettingsRepository {

    private object PreferencesKeys {
        // General
        val HOMEPAGE = stringPreferencesKey("homepage")
        val SEARCH_ENGINE = stringPreferencesKey("search_engine")
        val NEW_TAB_BEHAVIOR = stringPreferencesKey("new_tab_behavior")

        // Privacy
        val ENABLE_COOKIES = booleanPreferencesKey("enable_cookies")
        val ENABLE_THIRD_PARTY_COOKIES = booleanPreferencesKey("enable_third_party_cookies")
        val CLEAR_HISTORY_ON_EXIT = booleanPreferencesKey("clear_history_on_exit")
        val CLEAR_CACHE_ON_EXIT = booleanPreferencesKey("clear_cache_on_exit")

        // Appearance
        val TEXT_SCALE = floatPreferencesKey("text_scale")
        val FORCE_ZOOM_ENABLED = booleanPreferencesKey("force_zoom_enabled")

        // Advanced
        val ENABLE_JAVASCRIPT = booleanPreferencesKey("enable_javascript")
        val ENABLE_DOM_STORAGE = booleanPreferencesKey("enable_dom_storage")
        val ENABLE_POPUP_BLOCKING = booleanPreferencesKey("enable_popup_blocking")
        val ENABLE_HARDWARE_ACCELERATION = booleanPreferencesKey("enable_hardware_acceleration")
        val USER_AGENT_STRING = stringPreferencesKey("user_agent_string")
    }

    override fun getUserPreferences(): Flow<UserPreferences> {
        return context.dataStore.data.map { preferences ->
            UserPreferences(
                // General
                homepage = preferences[PreferencesKeys.HOMEPAGE] ?: "https://www.google.com",
                searchEngine = preferences[PreferencesKeys.SEARCH_ENGINE]?.let {
                    SearchEngine.valueOf(it)
                } ?: SearchEngine.GOOGLE,
                openNewTabBehavior = preferences[PreferencesKeys.NEW_TAB_BEHAVIOR]?.let {
                    NewTabBehavior.valueOf(it)
                } ?: NewTabBehavior.HOMEPAGE,

                // Privacy
                enableCookies = preferences[PreferencesKeys.ENABLE_COOKIES] ?: true,
                enableThirdPartyCookies = preferences[PreferencesKeys.ENABLE_THIRD_PARTY_COOKIES] ?: false,
                clearHistoryOnExit = preferences[PreferencesKeys.CLEAR_HISTORY_ON_EXIT] ?: false,
                clearCacheOnExit = preferences[PreferencesKeys.CLEAR_CACHE_ON_EXIT] ?: false,

                // Appearance
                textScale = preferences[PreferencesKeys.TEXT_SCALE] ?: 1.0f,
                forceZoomEnabled = preferences[PreferencesKeys.FORCE_ZOOM_ENABLED] ?: true,

                // Advanced
                enableJavaScript = preferences[PreferencesKeys.ENABLE_JAVASCRIPT] ?: true,
                enableDomStorage = preferences[PreferencesKeys.ENABLE_DOM_STORAGE] ?: true,
                enablePopupBlocking = preferences[PreferencesKeys.ENABLE_POPUP_BLOCKING] ?: true,
                enableHardwareAcceleration = preferences[PreferencesKeys.ENABLE_HARDWARE_ACCELERATION] ?: true,
                userAgentString = preferences[PreferencesKeys.USER_AGENT_STRING] ?: ""
            )
        }
    }

    override suspend fun setHomepage(url: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.HOMEPAGE] = url
        }
    }

    override suspend fun setSearchEngine(searchEngine: SearchEngine) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.SEARCH_ENGINE] = searchEngine.name
        }
    }

    override suspend fun setNewTabBehavior(behavior: NewTabBehavior) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.NEW_TAB_BEHAVIOR] = behavior.name
        }
    }

    override suspend fun setEnableCookies(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_COOKIES] = enabled
        }
    }

    override suspend fun setEnableThirdPartyCookies(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_THIRD_PARTY_COOKIES] = enabled
        }
    }

    override suspend fun setClearHistoryOnExit(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CLEAR_HISTORY_ON_EXIT] = enabled
        }
    }

    override suspend fun setClearCacheOnExit(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.CLEAR_CACHE_ON_EXIT] = enabled
        }
    }

    override suspend fun setTextScale(scale: Float) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.TEXT_SCALE] = scale.coerceIn(0.8f, 1.5f)
        }
    }

    override suspend fun setForceZoomEnabled(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.FORCE_ZOOM_ENABLED] = enabled
        }
    }

    override suspend fun setEnableJavaScript(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_JAVASCRIPT] = enabled
        }
    }

    override suspend fun setEnableDomStorage(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_DOM_STORAGE] = enabled
        }
    }

    override suspend fun setEnablePopupBlocking(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_POPUP_BLOCKING] = enabled
        }
    }

    override suspend fun setEnableHardwareAcceleration(enabled: Boolean) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.ENABLE_HARDWARE_ACCELERATION] = enabled
        }
    }

    override suspend fun setUserAgentString(userAgent: String) {
        context.dataStore.edit { preferences ->
            preferences[PreferencesKeys.USER_AGENT_STRING] = userAgent
        }
    }

    override suspend fun resetToDefaults() {
        context.dataStore.edit { preferences ->
            preferences.clear()
        }
    }
}