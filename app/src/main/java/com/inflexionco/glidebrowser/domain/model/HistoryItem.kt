package com.inflexionco.glidebrowser.domain.model

data class HistoryItem(
    val id: Long = 0,
    val title: String,
    val url: String,
    val faviconUrl: String? = null,
    val visitedAt: Long = System.currentTimeMillis(),
    val visitCount: Int = 1
)