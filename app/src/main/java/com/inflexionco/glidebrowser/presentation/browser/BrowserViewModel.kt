package com.inflexionco.glidebrowser.presentation.browser

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BrowserViewModel @Inject constructor() : ViewModel() {

    private val _uiState = MutableStateFlow(BrowserUiState())
    val uiState: StateFlow<BrowserUiState> = _uiState.asStateFlow()

    fun updateUrl(url: String) {
        _uiState.value = _uiState.value.copy(currentUrl = url)
    }

    fun updateTitle(title: String) {
        _uiState.value = _uiState.value.copy(pageTitle = title)
    }

    fun updateLoadingProgress(progress: Int) {
        _uiState.value = _uiState.value.copy(
            isLoading = progress < 100,
            loadingProgress = progress
        )
    }

    fun canGoBack(canGoBack: Boolean) {
        _uiState.value = _uiState.value.copy(canGoBack = canGoBack)
    }

    fun canGoForward(canGoForward: Boolean) {
        _uiState.value = _uiState.value.copy(canGoForward = canGoForward)
    }

    fun onError(errorMessage: String) {
        _uiState.value = _uiState.value.copy(
            hasError = true,
            errorMessage = errorMessage,
            isLoading = false
        )
    }

    fun clearError() {
        _uiState.value = _uiState.value.copy(
            hasError = false,
            errorMessage = null
        )
    }
}

data class BrowserUiState(
    val currentUrl: String = "",
    val pageTitle: String = "",
    val isLoading: Boolean = false,
    val loadingProgress: Int = 0,
    val canGoBack: Boolean = false,
    val canGoForward: Boolean = false,
    val hasError: Boolean = false,
    val errorMessage: String? = null
)