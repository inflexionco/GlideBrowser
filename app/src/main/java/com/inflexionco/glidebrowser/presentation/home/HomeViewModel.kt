package com.inflexionco.glidebrowser.presentation.home

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inflexionco.glidebrowser.data.local.entity.FavoriteEntity
import com.inflexionco.glidebrowser.domain.model.HistoryItem
import com.inflexionco.glidebrowser.domain.repository.FavoriteRepository
import com.inflexionco.glidebrowser.domain.repository.HistoryRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.catch
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val favoriteRepository: FavoriteRepository,
    private val historyRepository: HistoryRepository
) : ViewModel() {

    private val _uiState = MutableStateFlow(HomeUiState())
    val uiState: StateFlow<HomeUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
        loadMostVisited()
        initializeDefaultFavorites()
    }

    private fun initializeDefaultFavorites() {
        viewModelScope.launch {
            try {
                // Check if user has any favorites
                val count = favoriteRepository.isFavorite("https://www.youtube.com")
                if (!count) {
                    // Add default favorites
                    val defaults = listOf(
                        Pair("YouTube", "https://www.youtube.com"),
                        Pair("Netflix", "https://www.netflix.com"),
                        Pair("Prime Video", "https://www.primevideo.com"),
                        Pair("Disney+", "https://www.disneyplus.com"),
                        Pair("Google", "https://www.google.com")
                    )

                    defaults.forEach { (title, url) ->
                        favoriteRepository.addFavorite(title, url)
                    }
                }
            } catch (e: Exception) {
                Timber.e(e, "Error initializing default favorites")
            }
        }
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            favoriteRepository.getAllFavorites()
                .catch { e ->
                    Timber.e(e, "Error loading favorites")
                    _uiState.update { it.copy(error = e.message) }
                }
                .collect { favorites ->
                    _uiState.update {
                        it.copy(
                            favorites = favorites,
                            isLoading = false,
                            error = null
                        )
                    }
                }
        }
    }

    private fun loadMostVisited() {
        viewModelScope.launch {
            historyRepository.getMostVisited(limit = 10, minVisits = 2)
                .catch { e ->
                    Timber.e(e, "Error loading most visited")
                }
                .collect { history ->
                    _uiState.update { it.copy(mostVisited = history) }
                }
        }
    }

    fun addFavorite(title: String, url: String) {
        viewModelScope.launch {
            try {
                favoriteRepository.addFavorite(title, url)
            } catch (e: Exception) {
                Timber.e(e, "Error adding favorite")
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    fun removeFavorite(url: String) {
        viewModelScope.launch {
            try {
                favoriteRepository.removeFavorite(url)
            } catch (e: Exception) {
                Timber.e(e, "Error removing favorite")
                _uiState.update { it.copy(error = e.message) }
            }
        }
    }

    suspend fun isFavorite(url: String): Boolean {
        return try {
            favoriteRepository.isFavorite(url)
        } catch (e: Exception) {
            Timber.e(e, "Error checking favorite status")
            false
        }
    }

    fun clearError() {
        _uiState.update { it.copy(error = null) }
    }
}

data class HomeUiState(
    val favorites: List<FavoriteEntity> = emptyList(),
    val mostVisited: List<HistoryItem> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null
)