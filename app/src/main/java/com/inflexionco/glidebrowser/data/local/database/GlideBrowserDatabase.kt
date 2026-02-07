package com.inflexionco.glidebrowser.data.local.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.inflexionco.glidebrowser.data.local.dao.BookmarkDao
import com.inflexionco.glidebrowser.data.local.dao.HistoryDao
import com.inflexionco.glidebrowser.data.local.dao.TabDao
import com.inflexionco.glidebrowser.data.local.entity.BookmarkEntity
import com.inflexionco.glidebrowser.data.local.entity.HistoryEntity
import com.inflexionco.glidebrowser.data.local.entity.TabEntity

@Database(
    entities = [
        BookmarkEntity::class,
        HistoryEntity::class,
        TabEntity::class
    ],
    version = 1,
    exportSchema = false
)
abstract class GlideBrowserDatabase : RoomDatabase() {

    abstract fun bookmarkDao(): BookmarkDao
    abstract fun historyDao(): HistoryDao
    abstract fun tabDao(): TabDao

    companion object {
        const val DATABASE_NAME = "glide_browser_db"
    }
}