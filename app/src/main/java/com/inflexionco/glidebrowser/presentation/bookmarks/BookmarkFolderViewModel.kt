package com.inflexionco.glidebrowser.presentation.bookmarks

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inflexionco.glidebrowser.data.local.entity.BookmarkFolderEntity
import com.inflexionco.glidebrowser.domain.repository.BookmarkFolderRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class BookmarkFolderViewModel @Inject constructor(
    private val folderRepository: BookmarkFolderRepository
) : ViewModel() {

    fun getAllFolders(): Flow<List<BookmarkFolderEntity>> {
        return folderRepository.getAllFolders()
    }

    fun getRootFolders(): Flow<List<BookmarkFolderEntity>> {
        return folderRepository.getRootFolders()
    }

    fun getFoldersByParent(parentId: Long): Flow<List<BookmarkFolderEntity>> {
        return folderRepository.getFoldersByParent(parentId)
    }

    fun createFolder(name: String, parentFolderId: Long? = null) {
        viewModelScope.launch {
            folderRepository.createFolder(name, parentFolderId)
        }
    }

    fun updateFolder(id: Long, name: String) {
        viewModelScope.launch {
            folderRepository.updateFolder(id, name)
        }
    }

    fun deleteFolder(id: Long) {
        viewModelScope.launch {
            folderRepository.deleteFolder(id)
        }
    }
}