package com.inflexionco.glidebrowser.data.repository

import com.inflexionco.glidebrowser.data.local.dao.TabDao
import com.inflexionco.glidebrowser.data.local.entity.TabEntity
import com.inflexionco.glidebrowser.domain.model.Tab
import com.inflexionco.glidebrowser.domain.repository.TabRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TabRepositoryImpl @Inject constructor(
    private val tabDao: TabDao
) : TabRepository {

    override fun getAllTabs(): Flow<List<Tab>> {
        return tabDao.getAllTabs().map { entities ->
            entities.map { it.toDomain() }
        }
    }

    override fun getActiveTab(): Flow<Tab?> {
        return tabDao.getActiveTab().map { it?.toDomain() }
    }

    override suspend fun getTabById(id: Long): Tab? {
        return tabDao.getTabById(id)?.toDomain()
    }

    override suspend fun createTab(url: String, title: String?, position: Int): Long {
        Timber.d("Creating new tab: url=$url, title=$title, position=$position")
        val tabEntity = TabEntity(
            url = url,
            title = title,
            position = position,
            isActive = false
        )
        return tabDao.insertTab(tabEntity)
    }

    override suspend fun updateTab(tab: Tab) {
        Timber.d("Updating tab: id=${tab.id}, url=${tab.url}")
        tabDao.updateTab(tab.toEntity())
    }

    override suspend fun deleteTab(tabId: Long) {
        Timber.d("Deleting tab: id=$tabId")
        val tab = tabDao.getTabById(tabId)
        if (tab != null) {
            tabDao.deleteTab(tab)
        }
    }

    override suspend fun deleteAllTabs() {
        Timber.d("Deleting all tabs")
        tabDao.deleteAllTabs()
    }

    override suspend fun setActiveTab(tabId: Long) {
        Timber.d("Setting active tab: id=$tabId")
        tabDao.deactivateAllTabs()
        tabDao.setActiveTab(tabId)
    }

    override suspend fun closeTab(tabId: Long) {
        Timber.d("Closing tab: id=$tabId")
        val tab = tabDao.getTabById(tabId)
        if (tab != null) {
            tabDao.deleteTab(tab)
        }
    }

    override suspend fun updateTabUrl(tabId: Long, url: String) {
        Timber.d("Updating tab URL: id=$tabId, url=$url")
        val tab = tabDao.getTabById(tabId)
        if (tab != null) {
            tabDao.updateTab(tab.copy(url = url, updatedAt = System.currentTimeMillis()))
        }
    }

    override suspend fun updateTabTitle(tabId: Long, title: String) {
        Timber.d("Updating tab title: id=$tabId, title=$title")
        val tab = tabDao.getTabById(tabId)
        if (tab != null) {
            tabDao.updateTab(tab.copy(title = title, updatedAt = System.currentTimeMillis()))
        }
    }

    override suspend fun reorderTabs(tabs: List<Tab>) {
        Timber.d("Reordering ${tabs.size} tabs")
        tabs.forEachIndexed { index, tab ->
            val entity = tab.copy(position = index).toEntity()
            tabDao.updateTab(entity)
        }
    }

    private fun TabEntity.toDomain(): Tab {
        return Tab(
            id = id,
            url = url,
            title = title,
            position = position,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }

    private fun Tab.toEntity(): TabEntity {
        return TabEntity(
            id = id,
            url = url,
            title = title,
            position = position,
            isActive = isActive,
            createdAt = createdAt,
            updatedAt = updatedAt
        )
    }
}