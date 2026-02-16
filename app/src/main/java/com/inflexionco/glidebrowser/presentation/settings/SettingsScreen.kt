package com.inflexionco.glidebrowser.presentation.settings

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.BuildConfig
import com.inflexionco.glidebrowser.presentation.settings.components.SettingSectionHeader
import com.inflexionco.glidebrowser.presentation.settings.components.SettingTextItem
import com.inflexionco.glidebrowser.presentation.settings.components.SettingToggleItem
import com.inflexionco.glidebrowser.ui.components.TvButton
import com.inflexionco.glidebrowser.ui.theme.Dimens

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun SettingsScreen(
    onNavigateBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: SettingsViewModel = hiltViewModel()
) {
    val preferences by viewModel.userPreferences.collectAsState()
    val isLoading by viewModel.isLoading.collectAsState()
    val backButtonFocusRequester = remember { FocusRequester() }

    // Request focus on back button when screen loads
    LaunchedEffect(Unit) {
        backButtonFocusRequester.requestFocus()
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .background(MaterialTheme.colorScheme.background)
            .padding(Dimens.spacing24)
    ) {
        // Header with back button
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically
        ) {
            Column {
                Text(
                    text = "Settings",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "Customize your browsing experience",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Dimens.spacing4)
                )
            }

            Row(
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
            ) {
                TvButton(
                    text = "Reset All",
                    onClick = { viewModel.resetToDefaults() },
                    enabled = !isLoading
                )
                TvButton(
                    text = "Back",
                    onClick = onNavigateBack,
                    icon = Icons.AutoMirrored.Filled.ArrowBack,
                    modifier = Modifier.focusRequester(backButtonFocusRequester)
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing24))

        // Settings list
        LazyColumn(
            modifier = Modifier
                .fillMaxSize()
                .focusGroup(),
            contentPadding = PaddingValues(
                horizontal = Dimens.spacing8,
                vertical = Dimens.spacing8
            ),
            verticalArrangement = Arrangement.spacedBy(Dimens.spacing12)
        ) {
            // General Section
            item {
                SettingSectionHeader(title = "General")
            }

            item {
                SettingTextItem(
                    title = "Homepage",
                    value = preferences.homepage,
                    description = "The page that loads when you open a new tab"
                )
            }

            item {
                SettingTextItem(
                    title = "Search Engine",
                    value = preferences.searchEngine.displayName,
                    description = "Default search engine for address bar searches"
                )
            }

            item {
                SettingTextItem(
                    title = "New Tab Behavior",
                    value = preferences.openNewTabBehavior.displayName,
                    description = "What to show when opening a new tab"
                )
            }

            // Privacy Section
            item {
                SettingSectionHeader(title = "Privacy & Security")
            }

            item {
                SettingToggleItem(
                    title = "Enable Cookies",
                    description = "Allow websites to store cookies",
                    checked = preferences.enableCookies,
                    onCheckedChange = { viewModel.setEnableCookies(it) }
                )
            }

            item {
                SettingToggleItem(
                    title = "Third-Party Cookies",
                    description = "Allow third-party cookies for tracking and ads",
                    checked = preferences.enableThirdPartyCookies,
                    onCheckedChange = { viewModel.setEnableThirdPartyCookies(it) },
                    enabled = preferences.enableCookies
                )
            }

            item {
                SettingToggleItem(
                    title = "Block Pop-ups",
                    description = "Prevent pop-up windows from opening",
                    checked = preferences.enablePopupBlocking,
                    onCheckedChange = { viewModel.setEnablePopupBlocking(it) }
                )
            }

            item {
                SettingToggleItem(
                    title = "Clear History on Exit",
                    description = "Automatically clear browsing history when closing the app",
                    checked = preferences.clearHistoryOnExit,
                    onCheckedChange = { viewModel.setClearHistoryOnExit(it) }
                )
            }

            item {
                SettingToggleItem(
                    title = "Clear Cache on Exit",
                    description = "Automatically clear cached data when closing the app",
                    checked = preferences.clearCacheOnExit,
                    onCheckedChange = { viewModel.setClearCacheOnExit(it) }
                )
            }

            item {
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = Dimens.spacing8),
                    horizontalArrangement = Arrangement.Start
                ) {
                    TvButton(
                        text = "Clear All Browsing Data",
                        onClick = { viewModel.clearAllBrowsingData() },
                        enabled = !isLoading
                    )
                }
            }

            // Appearance Section
            item {
                SettingSectionHeader(title = "Appearance")
            }

            item {
                SettingTextItem(
                    title = "Text Scale",
                    value = "${(preferences.textScale * 100).toInt()}%",
                    description = "Adjust the size of text on web pages"
                )
            }

            item {
                SettingToggleItem(
                    title = "Force Enable Zoom",
                    description = "Allow zooming on all websites, even if they disable it",
                    checked = preferences.forceZoomEnabled,
                    onCheckedChange = { viewModel.setForceZoomEnabled(it) }
                )
            }

            // Downloads Section
            item {
                SettingSectionHeader(title = "Downloads")
            }

            item {
                SettingTextItem(
                    title = "Download Location",
                    value = "Internal Storage/Downloads",
                    description = "Where downloaded files are saved"
                )
            }

            item {
                SettingToggleItem(
                    title = "Ask Where to Save Files",
                    description = "Prompt for location before each download",
                    checked = false,
                    onCheckedChange = { /* TODO: Implement */ }
                )
            }

            item {
                SettingToggleItem(
                    title = "Download Notifications",
                    description = "Show notifications for download progress",
                    checked = true,
                    onCheckedChange = { /* TODO: Implement */ }
                )
            }

            // Display Section
            item {
                SettingSectionHeader(title = "Display")
            }

            item {
                SettingToggleItem(
                    title = "Fullscreen Mode",
                    description = "Hide status and navigation bars for immersive browsing",
                    checked = false,
                    onCheckedChange = { /* TODO: Implement */ }
                )
            }

            item {
                SettingTextItem(
                    title = "Display Resolution",
                    value = "Optimized for TV (1080p)",
                    description = "Automatic display optimization based on your TV"
                )
            }

            item {
                SettingToggleItem(
                    title = "Desktop Mode",
                    description = "Request desktop versions of websites",
                    checked = preferences.desktopMode,
                    onCheckedChange = { viewModel.setDesktopMode(it) }
                )
            }

            // Advanced Section
            item {
                SettingSectionHeader(title = "Advanced")
            }

            item {
                SettingToggleItem(
                    title = "Enable JavaScript",
                    description = "Allow websites to run JavaScript (recommended)",
                    checked = preferences.enableJavaScript,
                    onCheckedChange = { viewModel.setEnableJavaScript(it) }
                )
            }

            item {
                SettingToggleItem(
                    title = "Enable DOM Storage",
                    description = "Allow websites to store data locally",
                    checked = preferences.enableDomStorage,
                    onCheckedChange = { viewModel.setEnableDomStorage(it) }
                )
            }

            item {
                SettingToggleItem(
                    title = "Hardware Acceleration",
                    description = "Use GPU for faster rendering (recommended)",
                    checked = preferences.enableHardwareAcceleration,
                    onCheckedChange = { viewModel.setEnableHardwareAcceleration(it) }
                )
            }

            // About Section
            item {
                SettingSectionHeader(title = "About")
            }

            item {
                SettingTextItem(
                    title = "Version",
                    value = "${BuildConfig.VERSION_NAME} (${BuildConfig.VERSION_CODE})",
                    description = "Glide Browser for Android TV"
                )
            }

            item {
                SettingTextItem(
                    title = "Build Type",
                    value = BuildConfig.BUILD_TYPE,
                    description = "Current build configuration"
                )
            }

            // Bottom spacing
            item {
                Spacer(modifier = Modifier.height(Dimens.spacing24))
            }
        }
    }
}