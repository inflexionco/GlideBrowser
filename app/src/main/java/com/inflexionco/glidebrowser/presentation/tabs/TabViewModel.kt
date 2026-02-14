package com.inflexionco.glidebrowser.presentation.tabs

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inflexionco.glidebrowser.domain.model.Tab
import com.inflexionco.glidebrowser.domain.repository.TabRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

data class TabState(
    val tabs: List<Tab> = emptyList(),
    val activeTab: Tab? = null,
    val isLoading: Boolean = false,
    val error: String? = null
)

sealed class TabEvent {
    data class TabCreated(val tabId: Long) : TabEvent()
    data class TabClosed(val tabId: Long) : TabEvent()
    data class TabSwitched(val tabId: Long) : TabEvent()
    data class Error(val message: String) : TabEvent()
}

@HiltViewModel
class TabViewModel @Inject constructor(
    private val tabRepository: TabRepository
) : ViewModel() {

    private val _state = MutableStateFlow(TabState())
    val state: StateFlow<TabState> = _state.asStateFlow()

    private val _events = MutableStateFlow<TabEvent?>(null)
    val events: StateFlow<TabEvent?> = _events.asStateFlow()

    val tabs: StateFlow<List<Tab>> = tabRepository.getAllTabs()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = emptyList()
        )

    val activeTab: StateFlow<Tab?> = tabRepository.getActiveTab()
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5000),
            initialValue = null
        )

    init {
        Timber.d("TabViewModel initialized")
        observeTabs()
        initializeTabsOnStartup()
    }

    /**
     * Initialize tabs on app startup
     * If no tabs exist, create a default tab with Google homepage
     */
    private fun initializeTabsOnStartup() {
        viewModelScope.launch {
            try {
                val existingTabs = tabRepository.getAllTabs()

                // Collect once to check if tabs exist
                existingTabs.collect { tabList ->
                    if (tabList.isEmpty()) {
                        Timber.d("No existing tabs found, creating default tab")
                        createNewTab(
                            url = "https://www.google.com",
                            title = "Google"
                        )
                    } else {
                        Timber.d("Restored ${tabList.size} tabs from database")

                        // Ensure there's an active tab
                        val hasActiveTab = tabList.any { it.isActive }
                        if (!hasActiveTab && tabList.isNotEmpty()) {
                            Timber.d("No active tab found, setting first tab as active")
                            tabRepository.setActiveTab(tabList.first().id)
                        }
                    }
                    // Only check once on startup
                    return@collect
                }
            } catch (e: Exception) {
                Timber.e(e, "Error initializing tabs on startup")
            }
        }
    }

    private fun observeTabs() {
        viewModelScope.launch {
            tabs.collect { tabList ->
                _state.update { it.copy(tabs = tabList) }
            }
        }

        viewModelScope.launch {
            activeTab.collect { tab ->
                _state.update { it.copy(activeTab = tab) }
            }
        }
    }

    fun createNewTab(url: String = "https://www.google.com", title: String? = null) {
        viewModelScope.launch {
            try {
                _state.update { it.copy(isLoading = true) }
                Timber.d("Creating new tab with URL: $url")

                val position = _state.value.tabs.size
                val tabId = tabRepository.createTab(url, title, position)

                // Set the new tab as active
                tabRepository.setActiveTab(tabId)

                _events.value = TabEvent.TabCreated(tabId)
                Timber.d("Tab created successfully: id=$tabId")
            } catch (e: Exception) {
                Timber.e(e, "Error creating tab")
                _state.update { it.copy(error = e.message) }
                _events.value = TabEvent.Error(e.message ?: "Failed to create tab")
            } finally {
                _state.update { it.copy(isLoading = false) }
            }
        }
    }

    fun switchTab(tabId: Long) {
        viewModelScope.launch {
            try {
                Timber.d("Switching to tab: id=$tabId")
                tabRepository.setActiveTab(tabId)
                _events.value = TabEvent.TabSwitched(tabId)
            } catch (e: Exception) {
                Timber.e(e, "Error switching tab")
                _state.update { it.copy(error = e.message) }
                _events.value = TabEvent.Error(e.message ?: "Failed to switch tab")
            }
        }
    }

    fun closeTab(tabId: Long) {
        viewModelScope.launch {
            try {
                Timber.d("Closing tab: id=$tabId")
                val currentTabs = _state.value.tabs
                val isActiveTab = _state.value.activeTab?.id == tabId

                tabRepository.closeTab(tabId)

                // If we closed the active tab, switch to another tab
                if (isActiveTab && currentTabs.size > 1) {
                    val nextTab = currentTabs.firstOrNull { it.id != tabId }
                    nextTab?.let { tabRepository.setActiveTab(it.id) }
                }

                _events.value = TabEvent.TabClosed(tabId)
            } catch (e: Exception) {
                Timber.e(e, "Error closing tab")
                _state.update { it.copy(error = e.message) }
                _events.value = TabEvent.Error(e.message ?: "Failed to close tab")
            }
        }
    }

    fun closeAllTabs() {
        viewModelScope.launch {
            try {
                Timber.d("Closing all tabs")
                tabRepository.deleteAllTabs()
            } catch (e: Exception) {
                Timber.e(e, "Error closing all tabs")
                _state.update { it.copy(error = e.message) }
                _events.value = TabEvent.Error(e.message ?: "Failed to close all tabs")
            }
        }
    }

    fun updateTabUrl(tabId: Long, url: String) {
        viewModelScope.launch {
            try {
                Timber.d("Updating tab URL: id=$tabId, url=$url")
                tabRepository.updateTabUrl(tabId, url)
            } catch (e: Exception) {
                Timber.e(e, "Error updating tab URL")
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateTabTitle(tabId: Long, title: String) {
        viewModelScope.launch {
            try {
                Timber.d("Updating tab title: id=$tabId, title=$title")
                tabRepository.updateTabTitle(tabId, title)
            } catch (e: Exception) {
                Timber.e(e, "Error updating tab title")
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun updateTabThumbnail(tabId: Long, thumbnailPath: String) {
        viewModelScope.launch {
            try {
                Timber.d("Updating tab thumbnail: id=$tabId, path=$thumbnailPath")
                tabRepository.updateTabThumbnail(tabId, thumbnailPath)
            } catch (e: Exception) {
                Timber.e(e, "Error updating tab thumbnail")
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun reorderTabs(tabs: List<Tab>) {
        viewModelScope.launch {
            try {
                Timber.d("Reordering ${tabs.size} tabs")
                tabRepository.reorderTabs(tabs)
            } catch (e: Exception) {
                Timber.e(e, "Error reordering tabs")
                _state.update { it.copy(error = e.message) }
            }
        }
    }

    fun clearEvent() {
        _events.value = null
    }

    fun clearError() {
        _state.update { it.copy(error = null) }
    }

    override fun onCleared() {
        super.onCleared()
        Timber.d("TabViewModel cleared")
    }
}