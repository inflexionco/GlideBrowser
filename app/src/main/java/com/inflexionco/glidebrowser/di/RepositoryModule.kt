package com.inflexionco.glidebrowser.di

import com.inflexionco.glidebrowser.data.repository.FavoriteRepositoryImpl
import com.inflexionco.glidebrowser.data.repository.TabRepositoryImpl
import com.inflexionco.glidebrowser.domain.repository.FavoriteRepository
import com.inflexionco.glidebrowser.domain.repository.TabRepository
import dagger.Binds
import dagger.Module
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
abstract class RepositoryModule {

    @Binds
    @Singleton
    abstract fun bindTabRepository(
        tabRepositoryImpl: TabRepositoryImpl
    ): TabRepository

    @Binds
    @Singleton
    abstract fun bindFavoriteRepository(
        favoriteRepositoryImpl: FavoriteRepositoryImpl
    ): FavoriteRepository
}