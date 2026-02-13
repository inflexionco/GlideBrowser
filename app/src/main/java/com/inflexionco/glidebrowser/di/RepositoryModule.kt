package com.inflexionco.glidebrowser.di

import com.inflexionco.glidebrowser.data.repository.BookmarkFolderRepositoryImpl
import com.inflexionco.glidebrowser.data.repository.BookmarkRepositoryImpl
import com.inflexionco.glidebrowser.data.repository.DownloadRepositoryImpl
import com.inflexionco.glidebrowser.data.repository.FavoriteRepositoryImpl
import com.inflexionco.glidebrowser.data.repository.HistoryRepositoryImpl
import com.inflexionco.glidebrowser.data.repository.SettingsRepositoryImpl
import com.inflexionco.glidebrowser.data.repository.TabRepositoryImpl
import com.inflexionco.glidebrowser.domain.repository.BookmarkFolderRepository
import com.inflexionco.glidebrowser.domain.repository.BookmarkRepository
import com.inflexionco.glidebrowser.domain.repository.DownloadRepository
import com.inflexionco.glidebrowser.domain.repository.FavoriteRepository
import com.inflexionco.glidebrowser.domain.repository.HistoryRepository
import com.inflexionco.glidebrowser.domain.repository.SettingsRepository
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

    @Binds
    @Singleton
    abstract fun bindBookmarkRepository(
        bookmarkRepositoryImpl: BookmarkRepositoryImpl
    ): BookmarkRepository

    @Binds
    @Singleton
    abstract fun bindBookmarkFolderRepository(
        bookmarkFolderRepositoryImpl: BookmarkFolderRepositoryImpl
    ): BookmarkFolderRepository

    @Binds
    @Singleton
    abstract fun bindHistoryRepository(
        historyRepositoryImpl: HistoryRepositoryImpl
    ): HistoryRepository

    @Binds
    @Singleton
    abstract fun bindSettingsRepository(
        settingsRepositoryImpl: SettingsRepositoryImpl
    ): SettingsRepository

    @Binds
    @Singleton
    abstract fun bindDownloadRepository(
        downloadRepositoryImpl: DownloadRepositoryImpl
    ): DownloadRepository
}