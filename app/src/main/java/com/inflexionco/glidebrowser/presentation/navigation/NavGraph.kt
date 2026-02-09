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
import com.inflexionco.glidebrowser.presentation.browser.BrowserScreen
import com.inflexionco.glidebrowser.presentation.browser.EnhancedBrowserScreen
import com.inflexionco.glidebrowser.presentation.home.HomeScreen
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
            // Placeholder Settings Screen
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(16.dp)
                ) {
                    Text(
                        text = "Settings & Menu",
                        style = MaterialTheme.typography.headlineMedium
                    )
                    Text(
                        text = "Coming Soon - Will include Settings, Bookmarks, and History",
                        style = MaterialTheme.typography.bodyMedium
                    )
                    Spacer(modifier = Modifier.height(16.dp))
                    TvButton(
                        text = "Back to Browser",
                        onClick = { navController.popBackStack() }
                    )
                }
            }
        }

        // TODO: Add more destinations (Bookmarks, History)
    }
}