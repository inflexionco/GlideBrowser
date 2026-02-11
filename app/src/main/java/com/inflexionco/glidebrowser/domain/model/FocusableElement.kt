package com.inflexionco.glidebrowser.domain.model

import org.json.JSONObject

/**
 * Represents a focusable/clickable element on a web page
 */
data class FocusableElement(
    val id: String,
    val tagName: String,
    val type: String, // link, button, input, select, etc.
    val text: String,
    val href: String?,
    val x: Float,
    val y: Float,
    val width: Float,
    val height: Float,
    val isVisible: Boolean,
    val className: String?,
    val ariaLabel: String?
) {
    companion object {
        fun fromJson(json: JSONObject): FocusableElement? {
            return try {
                FocusableElement(
                    id = json.getString("id"),
                    tagName = json.getString("tagName"),
                    type = json.getString("type"),
                    text = json.optString("text", ""),
                    href = json.optString("href").takeIf { it.isNotEmpty() },
                    x = json.getDouble("x").toFloat(),
                    y = json.getDouble("y").toFloat(),
                    width = json.getDouble("width").toFloat(),
                    height = json.getDouble("height").toFloat(),
                    isVisible = json.getBoolean("isVisible"),
                    className = json.optString("className").takeIf { it.isNotEmpty() },
                    ariaLabel = json.optString("ariaLabel").takeIf { it.isNotEmpty() }
                )
            } catch (e: Exception) {
                null
            }
        }
    }

    /**
     * Calculate center point of element
     */
    val centerX: Float
        get() = x + (width / 2)

    val centerY: Float
        get() = y + (height / 2)

    /**
     * Check if point is inside element bounds
     */
    fun contains(pointX: Float, pointY: Float): Boolean {
        return pointX >= x && pointX <= (x + width) &&
               pointY >= y && pointY <= (y + height)
    }

    /**
     * Calculate distance to another element
     */
    fun distanceTo(other: FocusableElement): Float {
        val dx = centerX - other.centerX
        val dy = centerY - other.centerY
        return kotlin.math.sqrt(dx * dx + dy * dy)
    }
}