package com.inflexionco.glidebrowser.domain.repository

import com.inflexionco.glidebrowser.domain.model.Tab
import kotlinx.coroutines.flow.Flow

interface TabRepository {

    fun getAllTabs(): Flow<List<Tab>>

    fun getActiveTab(): Flow<Tab?>

    suspend fun getTabById(id: Long): Tab?

    suspend fun createTab(url: String, title: String?, position: Int): Long

    suspend fun updateTab(tab: Tab)

    suspend fun deleteTab(tabId: Long)

    suspend fun deleteAllTabs()

    suspend fun setActiveTab(tabId: Long)

    suspend fun closeTab(tabId: Long)

    suspend fun updateTabUrl(tabId: Long, url: String)

    suspend fun updateTabTitle(tabId: Long, title: String)

    suspend fun updateTabThumbnail(tabId: Long, thumbnailPath: String?)

    suspend fun reorderTabs(tabs: List<Tab>)
}