package com.inflexionco.glidebrowser.presentation.bookmarks

import androidx.compose.foundation.background
import androidx.compose.foundation.focusGroup
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.lazy.grid.items
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.filled.Search
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.Icon
import androidx.tv.material3.MaterialTheme
import androidx.tv.material3.Text
import com.inflexionco.glidebrowser.data.local.entity.BookmarkEntity
import com.inflexionco.glidebrowser.presentation.bookmarks.components.BookmarkCard
import com.inflexionco.glidebrowser.presentation.bookmarks.components.EditBookmarkDialog
import com.inflexionco.glidebrowser.presentation.browser.BookmarkViewModel
import com.inflexionco.glidebrowser.ui.components.TvButton
import com.inflexionco.glidebrowser.ui.theme.Dimens

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun BookmarksScreen(
    onNavigateBack: () -> Unit,
    onBookmarkClick: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: BookmarkViewModel = hiltViewModel()
) {
    val bookmarks by viewModel.getAllBookmarks().collectAsState(initial = emptyList())
    val backButtonFocusRequester = remember { FocusRequester() }
    var editingBookmark by remember { mutableStateOf<BookmarkEntity?>(null) }
    var searchQuery by remember { mutableStateOf("") }

    // Filter bookmarks based on search query
    val filteredBookmarks = remember(bookmarks, searchQuery) {
        if (searchQuery.isBlank()) {
            bookmarks
        } else {
            bookmarks.filter { bookmark ->
                bookmark.title.contains(searchQuery, ignoreCase = true) ||
                bookmark.url.contains(searchQuery, ignoreCase = true)
            }
        }
    }

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
                    text = "Bookmarks",
                    style = MaterialTheme.typography.headlineLarge,
                    color = MaterialTheme.colorScheme.onBackground
                )
                Text(
                    text = "${bookmarks.size} saved ${if (bookmarks.size == 1) "bookmark" else "bookmarks"}",
                    style = MaterialTheme.typography.titleMedium,
                    color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.7f),
                    modifier = Modifier.padding(top = Dimens.spacing4)
                )
            }

            TvButton(
                text = "Back",
                onClick = onNavigateBack,
                icon = Icons.AutoMirrored.Filled.ArrowBack,
                modifier = Modifier.focusRequester(backButtonFocusRequester)
            )
        }

        Spacer(modifier = Modifier.height(Dimens.spacing24))

        // Search bar
        if (bookmarks.isNotEmpty()) {
            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(
                        color = MaterialTheme.colorScheme.surfaceVariant,
                        shape = MaterialTheme.shapes.medium
                    )
                    .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing12),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Icon(
                    imageVector = Icons.Default.Search,
                    contentDescription = "Search",
                    tint = MaterialTheme.colorScheme.onSurfaceVariant,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(Dimens.spacing12))
                BasicTextField(
                    value = searchQuery,
                    onValueChange = { searchQuery = it },
                    modifier = Modifier.fillMaxWidth(),
                    textStyle = MaterialTheme.typography.bodyLarge.copy(
                        color = MaterialTheme.colorScheme.onSurface
                    ),
                    cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
                    decorationBox = { innerTextField ->
                        if (searchQuery.isEmpty()) {
                            Text(
                                text = "Search bookmarks...",
                                style = MaterialTheme.typography.bodyLarge,
                                color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.6f)
                            )
                        }
                        innerTextField()
                    }
                )
            }
        }

        Spacer(modifier = Modifier.height(Dimens.spacing24))

        // Bookmarks grid
        if (bookmarks.isEmpty()) {
            // Empty state
            Box(
                modifier = Modifier.fillMaxSize(),
                contentAlignment = Alignment.Center
            ) {
                Column(
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.spacedBy(Dimens.spacing16)
                ) {
                    Text(
                        text = "No Bookmarks Yet",
                        style = MaterialTheme.typography.headlineMedium,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.6f)
                    )
                    Text(
                        text = "Star your favorite websites to see them here",
                        style = MaterialTheme.typography.bodyLarge,
                        color = MaterialTheme.colorScheme.onBackground.copy(alpha = 0.4f)
                    )
                }
            }
        } else {
            LazyVerticalGrid(
                columns = GridCells.Fixed(5),
                modifier = Modifier
                    .fillMaxSize()
                    .focusGroup(),
                contentPadding = PaddingValues(
                    horizontal = Dimens.spacing8,
                    vertical = Dimens.spacing8
                ),
                horizontalArrangement = Arrangement.spacedBy(Dimens.spacing24),
                verticalArrangement = Arrangement.spacedBy(Dimens.spacing24)
            ) {
                items(filteredBookmarks, key = { it.id }) { bookmark ->
                    BookmarkCard(
                        title = bookmark.title,
                        url = bookmark.url,
                        onClick = { onBookmarkClick(bookmark.url) },
                        onEdit = { editingBookmark = bookmark },
                        onRemove = { viewModel.removeBookmark(bookmark.url) }
                    )
                }
            }
        }
    }

    // Edit bookmark dialog
    editingBookmark?.let { bookmark ->
        EditBookmarkDialog(
            currentTitle = bookmark.title,
            currentUrl = bookmark.url,
            onDismiss = { editingBookmark = null },
            onConfirm = { newTitle, newUrl ->
                // Ensure URL has protocol
                val finalUrl = if (!newUrl.startsWith("http://") && !newUrl.startsWith("https://")) {
                    "https://$newUrl"
                } else {
                    newUrl
                }
                viewModel.updateBookmark(bookmark.id, newTitle, finalUrl)
                editingBookmark = null
            }
        )
    }
}