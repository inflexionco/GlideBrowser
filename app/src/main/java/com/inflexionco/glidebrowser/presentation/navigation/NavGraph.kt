package com.inflexionco.glidebrowser.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.inflexionco.glidebrowser.presentation.browser.BrowserScreen
import com.inflexionco.glidebrowser.presentation.browser.EnhancedBrowserScreen
import com.inflexionco.glidebrowser.presentation.home.HomeScreen
import com.inflexionco.glidebrowser.presentation.tabs.TabSwitcherScreen

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
                }
            )
        }

        composable<Routes.Browser> { backStackEntry ->
            val browserRoute: Routes.Browser = backStackEntry.toRoute()
            EnhancedBrowserScreen(
                initialUrl = browserRoute.url ?: "https://www.google.com",
                onNavigateToTabs = {
                    navController.navigate(Routes.TabSwitcher)
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

        // TODO: Add more destinations (Settings, Bookmarks, History)
    }
}