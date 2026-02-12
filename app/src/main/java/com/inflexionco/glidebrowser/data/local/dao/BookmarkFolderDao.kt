package com.inflexionco.glidebrowser.data.local.dao

import androidx.room.*
import com.inflexionco.glidebrowser.data.local.entity.BookmarkFolderEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface BookmarkFolderDao {

    @Query("SELECT * FROM bookmark_folders WHERE parentFolderId IS NULL ORDER BY position ASC")
    fun getRootFolders(): Flow<List<BookmarkFolderEntity>>

    @Query("SELECT * FROM bookmark_folders WHERE parentFolderId = :parentId ORDER BY position ASC")
    fun getFoldersByParent(parentId: Long): Flow<List<BookmarkFolderEntity>>

    @Query("SELECT * FROM bookmark_folders WHERE id = :id")
    suspend fun getFolderById(id: Long): BookmarkFolderEntity?

    @Query("SELECT * FROM bookmark_folders ORDER BY position ASC")
    fun getAllFolders(): Flow<List<BookmarkFolderEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertFolder(folder: BookmarkFolderEntity): Long

    @Update
    suspend fun updateFolder(folder: BookmarkFolderEntity)

    @Delete
    suspend fun deleteFolder(folder: BookmarkFolderEntity)

    @Query("DELETE FROM bookmark_folders WHERE id = :id")
    suspend fun deleteFolderById(id: Long)

    @Query("SELECT COUNT(*) FROM bookmark_folders WHERE parentFolderId = :parentId")
    suspend fun getSubfolderCount(parentId: Long): Int
}