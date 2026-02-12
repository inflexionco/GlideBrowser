package com.inflexionco.glidebrowser.domain.repository

import com.inflexionco.glidebrowser.data.local.entity.BookmarkFolderEntity
import kotlinx.coroutines.flow.Flow

interface BookmarkFolderRepository {
    fun getAllFolders(): Flow<List<BookmarkFolderEntity>>
    fun getRootFolders(): Flow<List<BookmarkFolderEntity>>
    fun getFoldersByParent(parentId: Long): Flow<List<BookmarkFolderEntity>>
    suspend fun getFolderById(id: Long): BookmarkFolderEntity?
    suspend fun createFolder(name: String, parentFolderId: Long? = null): Long
    suspend fun updateFolder(id: Long, name: String)
    suspend fun deleteFolder(id: Long)
    suspend fun getSubfolderCount(parentId: Long): Int
}