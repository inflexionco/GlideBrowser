package com.inflexionco.glidebrowser.data.repository

import com.inflexionco.glidebrowser.data.local.dao.BookmarkFolderDao
import com.inflexionco.glidebrowser.data.local.entity.BookmarkFolderEntity
import com.inflexionco.glidebrowser.domain.repository.BookmarkFolderRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class BookmarkFolderRepositoryImpl @Inject constructor(
    private val folderDao: BookmarkFolderDao
) : BookmarkFolderRepository {

    override fun getAllFolders(): Flow<List<BookmarkFolderEntity>> {
        return folderDao.getAllFolders()
    }

    override fun getRootFolders(): Flow<List<BookmarkFolderEntity>> {
        return folderDao.getRootFolders()
    }

    override fun getFoldersByParent(parentId: Long): Flow<List<BookmarkFolderEntity>> {
        return folderDao.getFoldersByParent(parentId)
    }

    override suspend fun getFolderById(id: Long): BookmarkFolderEntity? {
        return folderDao.getFolderById(id)
    }

    override suspend fun createFolder(name: String, parentFolderId: Long?): Long {
        val folder = BookmarkFolderEntity(
            name = name,
            parentFolderId = parentFolderId
        )
        return folderDao.insertFolder(folder)
    }

    override suspend fun updateFolder(id: Long, name: String) {
        folderDao.getFolderById(id)?.let { folder ->
            folderDao.updateFolder(folder.copy(name = name))
        }
    }

    override suspend fun deleteFolder(id: Long) {
        folderDao.deleteFolderById(id)
    }

    override suspend fun getSubfolderCount(parentId: Long): Int {
        return folderDao.getSubfolderCount(parentId)
    }
}