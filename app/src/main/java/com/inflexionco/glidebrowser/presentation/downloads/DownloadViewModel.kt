package com.inflexionco.glidebrowser.presentation.downloads

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.inflexionco.glidebrowser.data.local.entity.DownloadStatus
import com.inflexionco.glidebrowser.domain.model.DownloadItem
import com.inflexionco.glidebrowser.domain.repository.DownloadRepository
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import timber.log.Timber
import javax.inject.Inject

@HiltViewModel
class DownloadViewModel @Inject constructor(
    private val downloadRepository: DownloadRepository
) : ViewModel() {

    private val _selectedFilter = MutableStateFlow(DownloadFilter.ALL)
    val selectedFilter = _selectedFilter.asStateFlow()

    private val allDownloads = downloadRepository.getAllDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val downloads: StateFlow<List<DownloadItem>> = combine(
        allDownloads,
        _selectedFilter
    ) { downloads, filter ->
        when (filter) {
            DownloadFilter.ALL -> downloads
            DownloadFilter.ACTIVE -> downloads.filter { it.isActive }
            DownloadFilter.COMPLETED -> downloads.filter { it.isComplete }
            DownloadFilter.FAILED -> downloads.filter { it.status == DownloadStatus.FAILED }
        }
    }.stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    val activeDownloads = downloadRepository.getActiveDownloads()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), emptyList())

    fun setFilter(filter: DownloadFilter) {
        _selectedFilter.value = filter
        Timber.d("Download filter changed to: $filter")
    }

    fun pauseDownload(downloadId: Long) {
        viewModelScope.launch {
            try {
                downloadRepository.pauseDownload(downloadId)
                Timber.d("Paused download: $downloadId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to pause download: $downloadId")
            }
        }
    }

    fun resumeDownload(downloadId: Long) {
        viewModelScope.launch {
            try {
                downloadRepository.resumeDownload(downloadId)
                Timber.d("Resumed download: $downloadId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to resume download: $downloadId")
            }
        }
    }

    fun cancelDownload(downloadId: Long) {
        viewModelScope.launch {
            try {
                downloadRepository.cancelDownload(downloadId)
                Timber.d("Cancelled download: $downloadId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to cancel download: $downloadId")
            }
        }
    }

    fun deleteDownload(downloadId: Long) {
        viewModelScope.launch {
            try {
                downloadRepository.deleteDownload(downloadId)
                Timber.d("Deleted download: $downloadId")
            } catch (e: Exception) {
                Timber.e(e, "Failed to delete download: $downloadId")
            }
        }
    }

    fun clearCompletedDownloads() {
        viewModelScope.launch {
            try {
                downloadRepository.clearCompletedDownloads()
                Timber.d("Cleared completed downloads")
            } catch (e: Exception) {
                Timber.e(e, "Failed to clear completed downloads")
            }
        }
    }

    fun clearAllDownloads() {
        viewModelScope.launch {
            try {
                downloadRepository.clearAllDownloads()
                Timber.d("Cleared all downloads")
            } catch (e: Exception) {
                Timber.e(e, "Failed to clear all downloads")
            }
        }
    }
}

enum class DownloadFilter {
    ALL,
    ACTIVE,
    COMPLETED,
    FAILED
}