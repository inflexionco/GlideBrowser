package com.inflexionco.glidebrowser.presentation.tabs.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Add
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.tv.material3.ExperimentalTvMaterial3Api
import androidx.tv.material3.MaterialTheme
import com.inflexionco.glidebrowser.domain.model.Tab
import com.inflexionco.glidebrowser.ui.components.TvIconButton
import com.inflexionco.glidebrowser.ui.theme.Dimens

@OptIn(ExperimentalTvMaterial3Api::class)
@Composable
fun TabBar(
    tabs: List<Tab>,
    activeTabId: Long?,
    onTabClick: (Long) -> Unit,
    onTabClose: (Long) -> Unit,
    onNewTabClick: () -> Unit,
    modifier: Modifier = Modifier
) {
    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(56.dp)
            .background(MaterialTheme.colorScheme.surface)
            .padding(horizontal = Dimens.spacing16, vertical = Dimens.spacing8),
        horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8),
        verticalAlignment = Alignment.CenterVertically
    ) {
        // Tab list
        LazyRow(
            modifier = Modifier.weight(1f),
            horizontalArrangement = Arrangement.spacedBy(Dimens.spacing8)
        ) {
            items(tabs, key = { it.id }) { tab ->
                CompactTabItem(
                    tab = tab,
                    isActive = tab.id == activeTabId,
                    onClick = { onTabClick(tab.id) },
                    onClose = { onTabClose(tab.id) }
                )
            }
        }

        Spacer(modifier = Modifier.width(Dimens.spacing8))

        // New tab button
        TvIconButton(
            onClick = onNewTabClick,
            icon = Icons.Default.Add,
            contentDescription = "New tab"
        )
    }
}