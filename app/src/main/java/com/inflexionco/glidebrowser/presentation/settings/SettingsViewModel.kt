package com.inflexionco.glidebrowser.presentation.settings

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inflexionco.glidebrowser.domain.model.NewTabBehavior
import com.inflexionco.glidebrowser.domain.model.SearchEngine
import com.inflexionco.glidebrowser.domain.model.UserPreferences
import com.inflexionco.glidebrowser.domain.repository.BookmarkRepository
import com.inflexionco.glidebrowser.domain.repository.HistoryRepository
import com.inflexionco.glidebrowser.domain.repository.SettingsRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsRepository: SettingsRepository,
    private val historyRepository: HistoryRepository,
    private val bookmarkRepository: BookmarkRepository
) : ViewModel() {

    val userPreferences: StateFlow<UserPreferences> = settingsRepository.getUserPreferences()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = UserPreferences()
        )

    private val _isLoading = MutableStateFlow(false)
    val isLoading: StateFlow<Boolean> = _isLoading.asStateFlow()

    // General settings
    fun setHomepage(url: String) {
        viewModelScope.launch {
            try {
                settingsRepository.setHomepage(url)
                Timber.d("Homepage set to: $url")
            } catch (e: Exception) {
                Timber.e(e, "Error setting homepage")
            }
        }
    }

    fun setSearchEngine(searchEngine: SearchEngine) {
        viewModelScope.launch {
            try {
                settingsRepository.setSearchEngine(searchEngine)
                Timber.d("Search engine set to: ${searchEngine.displayName}")
            } catch (e: Exception) {
                Timber.e(e, "Error setting search engine")
            }
        }
    }

    fun setNewTabBehavior(behavior: NewTabBehavior) {
        viewModelScope.launch {
            try {
                settingsRepository.setNewTabBehavior(behavior)
                Timber.d("New tab behavior set to: ${behavior.displayName}")
            } catch (e: Exception) {
                Timber.e(e, "Error setting new tab behavior")
            }
        }
    }

    // Privacy settings
    fun setEnableCookies(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setEnableCookies(enabled)
                Timber.d("Cookies enabled: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error setting cookies")
            }
        }
    }

    fun setEnableThirdPartyCookies(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setEnableThirdPartyCookies(enabled)
                Timber.d("Third-party cookies enabled: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error setting third-party cookies")
            }
        }
    }

    fun setClearHistoryOnExit(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setClearHistoryOnExit(enabled)
                Timber.d("Clear history on exit: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error setting clear history on exit")
            }
        }
    }

    fun setClearCacheOnExit(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setClearCacheOnExit(enabled)
                Timber.d("Clear cache on exit: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error setting clear cache on exit")
            }
        }
    }

    fun clearAllBrowsingData() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                historyRepository.clearAllHistory()
                // Note: WebView cache clearing would be done at the WebView level
                Timber.d("All browsing data cleared")
            } catch (e: Exception) {
                Timber.e(e, "Error clearing browsing data")
            } finally {
                _isLoading.value = false
            }
        }
    }

    // Appearance settings
    fun setTextScale(scale: Float) {
        viewModelScope.launch {
            try {
                settingsRepository.setTextScale(scale)
                Timber.d("Text scale set to: $scale")
            } catch (e: Exception) {
                Timber.e(e, "Error setting text scale")
            }
        }
    }

    fun setForceZoomEnabled(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setForceZoomEnabled(enabled)
                Timber.d("Force zoom enabled: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error setting force zoom")
            }
        }
    }

    // Advanced settings
    fun setEnableJavaScript(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setEnableJavaScript(enabled)
                Timber.d("JavaScript enabled: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error setting JavaScript")
            }
        }
    }

    fun setEnableDomStorage(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setEnableDomStorage(enabled)
                Timber.d("DOM storage enabled: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error setting DOM storage")
            }
        }
    }

    fun setEnablePopupBlocking(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setEnablePopupBlocking(enabled)
                Timber.d("Popup blocking enabled: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error setting popup blocking")
            }
        }
    }

    fun setEnableHardwareAcceleration(enabled: Boolean) {
        viewModelScope.launch {
            try {
                settingsRepository.setEnableHardwareAcceleration(enabled)
                Timber.d("Hardware acceleration enabled: $enabled")
            } catch (e: Exception) {
                Timber.e(e, "Error setting hardware acceleration")
            }
        }
    }

    fun setUserAgentString(userAgent: String) {
        viewModelScope.launch {
            try {
                settingsRepository.setUserAgentString(userAgent)
                Timber.d("User agent set to: $userAgent")
            } catch (e: Exception) {
                Timber.e(e, "Error setting user agent")
            }
        }
    }

    fun resetToDefaults() {
        viewModelScope.launch {
            _isLoading.value = true
            try {
                settingsRepository.resetToDefaults()
                Timber.d("Settings reset to defaults")
            } catch (e: Exception) {
                Timber.e(e, "Error resetting settings")
            } finally {
                _isLoading.value = false
            }
        }
    }
}