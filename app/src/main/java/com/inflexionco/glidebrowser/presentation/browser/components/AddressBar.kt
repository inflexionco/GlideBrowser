package com.inflexionco.glidebrowser.presentation.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.collectIsFocusedAsState
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Star
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.hilt.navigation.compose.hiltViewModel
import com.inflexionco.glidebrowser.presentation.browser.SuggestionViewModel
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.ui.components.TvIconButton
import com.inflexionco.glidebrowser.ui.components.TvTabIconButton
import com.inflexionco.glidebrowser.ui.theme.Dimens

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AddressBar(
    url: String,
    isLoading: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int = 1,
    isBookmarked: Boolean = false,
    onUrlSubmit: (String) -> Unit,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onStopClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTabsClick: () -> Unit = {},
    onNewTabClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    AddressBarInternal(
        url = url,
        isLoading = isLoading,
        canGoBack = canGoBack,
        canGoForward = canGoForward,
        tabCount = tabCount,
        isBookmarked = isBookmarked,
        onUrlSubmit = onUrlSubmit,
        onTextChanged = {},
        onBackClick = onBackClick,
        onForwardClick = onForwardClick,
        onRefreshClick = onRefreshClick,
        onStopClick = onStopClick,
        onHomeClick = onHomeClick,
        onTabsClick = onTabsClick,
        onNewTabClick = onNewTabClick,
        onBookmarkClick = onBookmarkClick,
        onMenuClick = onMenuClick,
        modifier = modifier
    )
}

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
internal fun AddressBarInternal(
    url: String,
    isLoading: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    tabCount: Int = 1,
    isBookmarked: Boolean = false,
    onUrlSubmit: (String) -> Unit,
    onTextChanged: (String) -> Unit,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onStopClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTabsClick: () -> Unit = {},
    onNewTabClick: () -> Unit = {},
    onBookmarkClick: () -> Unit = {},
    onMenuClick: () -> Unit = {},
    modifier: Modifier = Modifier
) {
    var textFieldValue by remember(url) { mutableStateOf(url) }
    var isEditing by remember { mutableStateOf(false) }
    val focusRequester = remember { FocusRequester() }

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(Dimens.urlBarHeight)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing8),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // 1. Home button - First position
        TvIconButton(
            onClick = onHomeClick,
            icon = Icons.Default.Home,
            contentDescription = "Go home"
        )

        // 2. Search bar (URL TextField) with bookmark button - Second position
        Box(
            modifier = Modifier
                .weight(1f)
                .height(Dimens.buttonHeight)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = RoundedCornerShape(28.dp)
                )
                .padding(horizontal = Dimens.spacing16),
            contentAlignment = Alignment.CenterStart
        ) {
            Row(
                verticalAlignment = Alignment.CenterVertically,
                modifier = Modifier.fillMaxWidth()
            ) {
                // URL Text Field
                BasicTextField(
                    value = textFieldValue,
                    onValueChange = {
                        textFieldValue = it
                        isEditing = true
                        onTextChanged(it)
                    },
                    modifier = Modifier
                        .weight(1f)
                        .focusRequester(focusRequester),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    singleLine = true,
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    keyboardOptions = KeyboardOptions(
                        keyboardType = KeyboardType.Uri,
                        imeAction = ImeAction.Go
                    ),
                    keyboardActions = KeyboardActions(
                        onGo = {
                            onUrlSubmit(textFieldValue)
                            isEditing = false
                        }
                    ),
                    decorationBox = { innerTextField ->
                        Box(modifier = Modifier.fillMaxWidth()) {
                            if (textFieldValue.isEmpty()) {
                                Text(
                                    text = "Enter URL or search",
                                    style = MaterialTheme.typography.bodyLarge,
                                    color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                                )
                            }
                            innerTextField()
                        }
                    }
                )

                // Bookmark/Favorite star icon - Inside search bar with focus indication
                if (url.isNotEmpty() && !isEditing) {
                    Spacer(modifier = Modifier.width(Dimens.spacing8))

                    val bookmarkInteractionSource = remember { MutableInteractionSource() }
                    val isBookmarkFocused by bookmarkInteractionSource.collectIsFocusedAsState()

                    Box(
                        modifier = Modifier
                            .size(32.dp)
                            .background(
                                color = if (isBookmarkFocused)
                                    MaterialTheme.colorScheme.onSurface.copy(alpha = 0.2f)
                                else
                                    androidx.compose.ui.graphics.Color.Transparent,
                                shape = CircleShape
                            )
                            .clickable(
                                interactionSource = bookmarkInteractionSource,
                                indication = null
                            ) { onBookmarkClick() },
                        contentAlignment = Alignment.Center
                    ) {
                        Icon(
                            imageVector = Icons.Filled.Star,
                            contentDescription = if (isBookmarked) "Remove from bookmarks" else "Add to bookmarks",
                            modifier = Modifier.size(20.dp),
                            tint = if (isBookmarked)
                                MaterialTheme.colorScheme.primary
                            else
                                MaterialTheme.colorScheme.onSurface.copy(alpha = 0.5f)
                        )
                    }
                }
            }
        }

        // 3. Reload button - Third position
        TvIconButton(
            onClick = if (isLoading) onStopClick else onRefreshClick,
            icon = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
            contentDescription = if (isLoading) "Stop loading" else "Refresh"
        )

        // 4. Forward and Back buttons - Fourth position
        TvIconButton(
            onClick = onBackClick,
            icon = Icons.Default.ArrowBack,
            contentDescription = "Go back",
            enabled = canGoBack
        )

        TvIconButton(
            onClick = onForwardClick,
            icon = Icons.Default.ArrowForward,
            contentDescription = "Go forward",
            enabled = canGoForward
        )

        // 5. Tabs button - Fifth position
        TvTabIconButton(
            onClick = onTabsClick,
            tabCount = tabCount,
            contentDescription = "Tabs ($tabCount)"
        )

        // 6. New tab button (+) - Sixth position
        TvIconButton(
            onClick = onNewTabClick,
            icon = Icons.Default.Add,
            contentDescription = "New tab"
        )

        // 7. Menu button removed as per requirements
    }

    // Update text field when URL changes externally (page navigation)
    LaunchedEffect(url) {
        if (!isEditing) {
            textFieldValue = url
        }
    }
}