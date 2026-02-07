package com.inflexionco.glidebrowser.presentation.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.toRoute
import com.inflexionco.glidebrowser.presentation.browser.BrowserScreen
import com.inflexionco.glidebrowser.presentation.home.HomeScreen

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
            BrowserScreen(
                initialUrl = browserRoute.url,
                onNavigateBack = {
                    navController.popBackStack()
                }
            )
        }

        // TODO: Add more destinations (Settings, Bookmarks, History)
    }
}