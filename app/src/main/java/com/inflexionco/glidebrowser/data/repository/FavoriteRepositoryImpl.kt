package com.inflexionco.glidebrowser.data.repository

import com.inflexionco.glidebrowser.data.local.dao.FavoriteDao
import com.inflexionco.glidebrowser.data.local.entity.FavoriteEntity
import com.inflexionco.glidebrowser.domain.repository.FavoriteRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class FavoriteRepositoryImpl @Inject constructor(
    private val favoriteDao: FavoriteDao
) : FavoriteRepository {

    override fun getAllFavorites(): Flow<List<FavoriteEntity>> {
        return favoriteDao.getAllFavorites()
    }

    override suspend fun addFavorite(title: String, url: String) {
        val count = favoriteDao.getFavoritesCount()
        val favorite = FavoriteEntity(
            title = title,
            url = url,
            position = count
        )
        favoriteDao.insertFavorite(favorite)
    }

    override suspend fun removeFavorite(url: String) {
        favoriteDao.deleteFavoriteByUrl(url)
    }

    override suspend fun isFavorite(url: String): Boolean {
        return favoriteDao.getFavoriteByUrl(url) != null
    }

    override suspend fun reorderFavorites(favorites: List<FavoriteEntity>) {
        favorites.forEachIndexed { index, favorite ->
            favoriteDao.updatePosition(favorite.id, index)
        }
    }
}