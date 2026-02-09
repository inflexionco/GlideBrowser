package com.inflexionco.glidebrowser.presentation.browser.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.ArrowBack
import androidx.compose.material.icons.filled.ArrowForward
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Menu
import androidx.compose.material.icons.filled.Refresh
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.ui.components.TvIconButton
import com.inflexionco.glidebrowser.ui.theme.Dimens

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun AddressBar(
    url: String,
    isLoading: Boolean,
    canGoBack: Boolean,
    canGoForward: Boolean,
    onUrlSubmit: (String) -> Unit,
    onBackClick: () -> Unit,
    onForwardClick: () -> Unit,
    onRefreshClick: () -> Unit,
    onStopClick: () -> Unit,
    onHomeClick: () -> Unit,
    onTabsClick: () -> Unit = {},
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
        // Back button
        TvIconButton(
            onClick = onBackClick,
            icon = Icons.Default.ArrowBack,
            contentDescription = "Go back",
            enabled = canGoBack
        )

        // Forward button
        TvIconButton(
            onClick = onForwardClick,
            icon = Icons.Default.ArrowForward,
            contentDescription = "Go forward",
            enabled = canGoForward
        )

        // Refresh/Stop button
        TvIconButton(
            onClick = if (isLoading) onStopClick else onRefreshClick,
            icon = if (isLoading) Icons.Default.Close else Icons.Default.Refresh,
            contentDescription = if (isLoading) "Stop loading" else "Refresh"
        )

        // Home button
        TvIconButton(
            onClick = onHomeClick,
            icon = Icons.Default.Home,
            contentDescription = "Go home"
        )

        Spacer(modifier = Modifier.width(Dimens.spacing8))

        // URL TextField
        Box(
            modifier = Modifier
                .weight(1f)
                .height(Dimens.buttonHeight)
                .background(
                    color = MaterialTheme.colorScheme.surfaceVariant,
                    shape = MaterialTheme.shapes.medium
                )
                .padding(horizontal = Dimens.spacing16),
            contentAlignment = Alignment.CenterStart
        ) {
            BasicTextField(
                value = textFieldValue,
                onValueChange = {
                    textFieldValue = it
                    isEditing = true
                },
                modifier = Modifier
                    .fillMaxWidth()
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
                    Row(
                        verticalAlignment = Alignment.CenterVertically,
                        modifier = Modifier.fillMaxWidth()
                    ) {
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
        }

        // Tabs button
        TvIconButton(
            onClick = onTabsClick,
            icon = Icons.Default.Menu,
            contentDescription = "Tabs"
        )

        // Search/Go button
        TvIconButton(
            onClick = {
                onUrlSubmit(textFieldValue)
                isEditing = false
            },
            icon = Icons.Default.Search,
            contentDescription = "Go"
        )
    }

    // Update text field when URL changes externally (page navigation)
    LaunchedEffect(url) {
        if (!isEditing) {
            textFieldValue = url
        }
    }
}