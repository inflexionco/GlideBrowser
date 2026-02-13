package com.inflexionco.glidebrowser.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.inflexionco.glidebrowser.data.local.dao.BookmarkDao
import com.inflexionco.glidebrowser.data.local.dao.BookmarkFolderDao
import com.inflexionco.glidebrowser.data.local.dao.DownloadDao
import com.inflexionco.glidebrowser.data.local.dao.FavoriteDao
import com.inflexionco.glidebrowser.data.local.dao.HistoryDao
import com.inflexionco.glidebrowser.data.local.dao.TabDao
import com.inflexionco.glidebrowser.data.local.entity.BookmarkEntity
import com.inflexionco.glidebrowser.data.local.entity.BookmarkFolderEntity
import com.inflexionco.glidebrowser.data.local.entity.DownloadEntity
import com.inflexionco.glidebrowser.data.local.entity.FavoriteEntity
import com.inflexionco.glidebrowser.data.local.entity.HistoryEntity
import com.inflexionco.glidebrowser.data.local.entity.TabEntity

@Database(
    entities = [
        BookmarkEntity::class,
        BookmarkFolderEntity::class,
        HistoryEntity::class,
        TabEntity::class,
        FavoriteEntity::class,
        DownloadEntity::class
    ],
    version = 7,
    exportSchema = false
)
abstract class GlideBrowserDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao
    abstract fun bookmarkFolderDao(): BookmarkFolderDao
    abstract fun historyDao(): HistoryDao
    abstract fun tabDao(): TabDao
    abstract fun favoriteDao(): FavoriteDao
    abstract fun downloadDao(): DownloadDao

    companion object {
        const val DATABASE_NAME = "glide_browser_db"
    }
}