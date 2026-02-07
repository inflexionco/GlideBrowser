package com.inflexionco.glidebrowser.di

import android.content.Context
import androidx.room.Room
import com.inflexionco.glidebrowser.data.local.dao.BookmarkDao
import com.inflexionco.glidebrowser.data.local.dao.HistoryDao
import com.inflexionco.glidebrowser.data.local.dao.TabDao
import com.inflexionco.glidebrowser.data.local.database.GlideBrowserDatabase
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.android.qualifiers.ApplicationContext
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object DatabaseModule {

    @Provides
    @Singleton
    fun provideDatabase(
        @ApplicationContext context: Context
    ): GlideBrowserDatabase {
        return Room.databaseBuilder(
            context,
            GlideBrowserDatabase::class.java,
            GlideBrowserDatabase.DATABASE_NAME
        )
            .fallbackToDestructiveMigration()
            .build()
    }

    @Provides
    fun provideBookmarkDao(database: GlideBrowserDatabase): BookmarkDao {
        return database.bookmarkDao()
    }

    @Provides
    fun provideHistoryDao(database: GlideBrowserDatabase): HistoryDao {
        return database.historyDao()
    }

    @Provides
    fun provideTabDao(database: GlideBrowserDatabase): TabDao {
        return database.tabDao()
    }
}