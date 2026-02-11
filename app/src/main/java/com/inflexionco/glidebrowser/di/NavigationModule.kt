package com.inflexionco.glidebrowser.di

import com.inflexionco.glidebrowser.domain.navigation.SpatialNavigationEngine
import com.inflexionco.glidebrowser.domain.navigation.WebNavigationManager
import com.inflexionco.glidebrowser.presentation.browser.navigation.DPadNavigationHandler
import dagger.Module
import dagger.Provides
import dagger.hilt.InstallIn
import dagger.hilt.components.SingletonComponent
import javax.inject.Singleton

@Module
@InstallIn(SingletonComponent::class)
object NavigationModule {

    @Provides
    @Singleton
    fun provideSpatialNavigationEngine(): SpatialNavigationEngine {
        return SpatialNavigationEngine()
    }

    @Provides
    @Singleton
    fun provideWebNavigationManager(
        spatialNavigation: SpatialNavigationEngine
    ): WebNavigationManager {
        return WebNavigationManager(spatialNavigation)
    }

    @Provides
    @Singleton
    fun provideDPadNavigationHandler(
        navigationManager: WebNavigationManager
    ): DPadNavigationHandler {
        return DPadNavigationHandler(navigationManager)
    }
}