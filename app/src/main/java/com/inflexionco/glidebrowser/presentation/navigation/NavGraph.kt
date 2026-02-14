package com.inflexionco.glidebrowser.presentation.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.presentation.bookmarks.BookmarksScreen
import com.inflexionco.glidebrowser.presentation.browser.BrowserScreen
import com.inflexionco.glidebrowser.presentation.browser.EnhancedBrowserScreen
import com.inflexionco.glidebrowser.presentation.downloads.DownloadsScreen
import com.inflexionco.glidebrowser.presentation.history.HistoryScreen
import com.inflexionco.glidebrowser.presentation.home.HomeScreen
import com.inflexionco.glidebrowser.presentation.settings.SettingsScreen
import com.inflexionco.glidebrowser.presentation.tabs.TabSwitcherScreen
import com.inflexionco.glidebrowser.ui.components.TvButton

@Composable
fun NavGraph(
    navController: NavHostController,
    modifier: Modifier = Modifier
) {
    NavHost(
        navController = navController,
        startDestination = Routes.Home,
        modifier = modifier
    ) {
        composable<Routes.Home> {
            HomeScreen(
                onNavigateToBrowser = { url ->
                    navController.navigate(Routes.Browser(url))
                },
                onNavigateToBookmarks = {
                    navController.navigate(Routes.Bookmarks)
                },
                onNavigateToHistory = {
                    navController.navigate(Routes.History)
                },
                onNavigateToDownloads = {
                    navController.navigate(Routes.Downloads)
                }
            )
        }

        composable<Routes.Browser> { backStackEntry ->
            val browserRoute: Routes.Browser = backStackEntry.toRoute()
            EnhancedBrowserScreen(
                initialUrl = browserRoute.url ?: "https://www.google.com",
                onNavigateToTabs = {
                    navController.navigate(Routes.TabSwitcher)
                },
                onNavigateToMenu = {
                    navController.navigate(Routes.Settings)
                }
            )
        }

        composable<Routes.TabSwitcher> {
            TabSwitcherScreen(
                onTabSelect = { tabId ->
                    // Switch to the selected tab
                    navController.navigate(Routes.Browser(null)) {
                        popUpTo(Routes.Home) { inclusive = false }
                    }
                },
                onNewTab = {
                    navController.navigate(Routes.Browser("https://www.google.com")) {
                        popUpTo(Routes.Home) { inclusive = false }
                    }
                }
            )
        }

        composable<Routes.Settings> {
            SettingsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }

        composable<Routes.Bookmarks> {
            BookmarksScreen(
                onNavigateBack = { navController.popBackStack() },
                onBookmarkClick = { url ->
                    navController.navigate(Routes.Browser(url)) {
                        popUpTo(Routes.Home) { inclusive = false }
                    }
                }
            )
        }

        composable<Routes.History> {
            HistoryScreen(
                onNavigateBack = { navController.popBackStack() },
                onHistoryItemClick = { url ->
                    navController.navigate(Routes.Browser(url)) {
                        popUpTo(Routes.Home) { inclusive = false }
                    }
                }
            )
        }

        composable<Routes.Downloads> {
            DownloadsScreen(
                onNavigateBack = { navController.popBackStack() }
            )
        }
    }
}