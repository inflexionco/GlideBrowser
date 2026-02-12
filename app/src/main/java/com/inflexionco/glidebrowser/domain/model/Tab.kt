package com.inflexionco.glidebrowser.domain.model

data class Tab(
    val id: Long = 0,
    val url: String,
    val title: String? = null,
    val position: Int,
    val isActive: Boolean = false,
    val thumbnailPath: String? = null,
    val createdAt: Long = System.currentTimeMillis(),
    val updatedAt: Long = System.currentTimeMillis()
)