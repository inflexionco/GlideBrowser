package com.inflexionco.glidebrowser.domain.repository

import com.inflexionco.glidebrowser.data.local.entity.FavoriteEntity
import kotlinx.coroutines.flow.Flow

interface FavoriteRepository {
    fun getAllFavorites(): Flow<List<FavoriteEntity>>
    suspend fun addFavorite(title: String, url: String)
    suspend fun removeFavorite(url: String)
    suspend fun isFavorite(url: String): Boolean
    suspend fun reorderFavorites(favorites: List<FavoriteEntity>)
}