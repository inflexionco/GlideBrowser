package com.inflexionco.glidebrowser.ui.components

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.slideInHorizontally
import androidx.compose.animation.slideOutHorizontally
import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.*
import androidx.compose.ui.unit.dp
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.input.key.Key
import androidx.compose.ui.input.key.key
import androidx.compose.ui.input.key.onPreviewKeyEvent
import androidx.tv.material3.*
import com.inflexionco.glidebrowser.ui.theme.Dimens

/**
 * Side navigation drawer for Android TV
 * Provides access to main app sections
 */
@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SideNavigationDrawer(
    isVisible: Boolean,
    onDismiss: () -> Unit,
    onNavigateToHome: () -> Unit,
    onNavigateToTabs: () -> Unit,
    onNavigateToHistory: () -> Unit,
    onNavigateToBookmarks: () -> Unit,
    onNavigateToDownloads: () -> Unit,
    onNavigateToSettings: () -> Unit,
    modifier: Modifier = Modifier
) {
    val closeButtonFocusRequester = remember { FocusRequester() }
    val listState = rememberLazyListState()

    // Auto-focus close button when drawer opens
    LaunchedEffect(isVisible) {
        if (isVisible) {
            closeButtonFocusRequester.requestFocus()
        }
    }

    AnimatedVisibility(
        visible = isVisible,
        enter = slideInHorizontally(initialOffsetX = { -it }),
        exit = slideOutHorizontally(targetOffsetX = { -it })
    ) {
        Surface(
            modifier = modifier
                .fillMaxHeight()
                .width(Dimens.navigationDrawerWidth)
                .background(MaterialTheme.colorScheme.surface)
                .focusGroup()
                .onPreviewKeyEvent { keyEvent ->
                    // Close drawer on right D-pad key press
                    if (keyEvent.key == Key.DirectionRight) {
                        onDismiss()
                        true
                    } else {
                        false
                    }
                },
            shape = MaterialTheme.shapes.medium
        ) {
            Column(
                modifier = Modifier
                    .fillMaxSize()
                    .padding(Dimens.spacing16)
            ) {
                // Header with close button
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(bottom = Dimens.spacing24),
                    horizontalArrangement = Arrangement.SpaceBetween,
                    verticalAlignment = Alignment.CenterVertically
                ) {
                    Text(
                        text = "Navigation",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    TvButton(
                        onClick = onDismiss,
                        text = "Close",
                        icon = Icons.AutoMirrored.Filled.ArrowBack,
                        modifier = Modifier.focusRequester(closeButtonFocusRequester)
                    )
                }

                // Navigation items
                LazyColumn(
                    state = listState,
                    modifier = Modifier.fillMaxSize(),
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacing12)
                ) {
                    items(navigationItems) { item ->
                        NavigationDrawerItem(
                            title = item.title,
                            icon = item.icon,
                            onClick = {
                                when (item.route) {
                                    "home" -> onNavigateToHome()
                                    "tabs" -> onNavigateToTabs()
                                    "history" -> onNavigateToHistory()
                                    "bookmarks" -> onNavigateToBookmarks()
                                    "downloads" -> onNavigateToDownloads()
                                    "settings" -> onNavigateToSettings()
                                }
                                onDismiss()
                            }
                        )
                    }
                }
            }
        }
    }
}

/**
 * Individual navigation item in the drawer
 */
@Composable
private fun NavigationDrawerItem(
    title: String,
    icon: ImageVector,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    TvButton(
        onClick = onClick,
        text = title,
        icon = icon,
        modifier = modifier.fillMaxWidth()
    )
}

/**
 * Navigation item data class
 */
private data class NavigationItem(
    val title: String,
    val icon: ImageVector,
    val route: String
)

/**
 * List of navigation items
 */
private val navigationItems = listOf(
    NavigationItem("Quick Access", Icons.Default.Home, "home"),
    NavigationItem("Tabs", Icons.Default.List, "tabs"),
    NavigationItem("History", Icons.Default.Info, "history"),
    NavigationItem("Bookmarks", Icons.Default.Star, "bookmarks"),
    NavigationItem("Downloads", Icons.Default.KeyboardArrowDown, "downloads"),
    NavigationItem("Settings", Icons.Default.Settings, "settings")
)