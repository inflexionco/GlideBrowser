package com.inflexionco.glidebrowser.domain.navigation

import com.inflexionco.glidebrowser.domain.model.FocusableElement
import com.inflexionco.glidebrowser.domain.model.NavigationDirection
import timber.log.Timber
import kotlin.math.abs
import kotlin.math.atan2
import kotlin.math.cos
import kotlin.math.sin

/**
 * Spatial navigation engine for finding the best next element to focus
 * based on direction and position
 */
class SpatialNavigationEngine {

    /**
     * Find the best next element to focus based on direction
     *
     * @param currentElement The currently focused element (null if none)
     * @param allElements All available focusable elements
     * @param direction The navigation direction
     * @param viewportWidth Current viewport width in pixels
     * @param viewportHeight Current viewport height in pixels
     * @param scrollX Current horizontal scroll position
     * @param scrollY Current vertical scroll position
     * @return The best element to focus next, or null if none found
     */
    fun findNextElement(
        currentElement: FocusableElement?,
        allElements: List<FocusableElement>,
        direction: NavigationDirection,
        viewportWidth: Float,
        viewportHeight: Float,
        scrollX: Float = 0f,
        scrollY: Float = 0f
    ): FocusableElement? {
        if (allElements.isEmpty()) {
            Timber.w("No elements available for navigation")
            return null
        }

        // If no current element, find the best starting element
        if (currentElement == null) {
            return findInitialElement(allElements, viewportWidth, viewportHeight, scrollX, scrollY)
        }

        // Filter candidates based on direction
        val candidates = filterCandidatesByDirection(
            currentElement,
            allElements.filter { it.id != currentElement.id },
            direction
        )

        if (candidates.isEmpty()) {
            Timber.d("No candidates found in direction $direction")
            return null
        }

        // Score candidates and find the best one
        return findBestCandidate(currentElement, candidates, direction)
    }

    /**
     * Find the best initial element to focus when none is selected
     */
    private fun findInitialElement(
        elements: List<FocusableElement>,
        viewportWidth: Float,
        viewportHeight: Float,
        scrollX: Float,
        scrollY: Float
    ): FocusableElement? {
        // Prefer elements in the visible viewport, near the top-left
        val viewportCenterX = scrollX + (viewportWidth / 2)
        val viewportCenterY = scrollY + (viewportHeight / 3) // Bias toward top

        return elements
            .filter { isInViewport(it, viewportWidth, viewportHeight, scrollX, scrollY) }
            .minByOrNull { element ->
                val dx = element.centerX - viewportCenterX
                val dy = element.centerY - viewportCenterY
                // Distance from top-left biased center
                kotlin.math.sqrt(dx * dx + dy * dy)
            } ?: elements.firstOrNull() // Fallback to first element
    }

    /**
     * Check if element is in the visible viewport
     */
    private fun isInViewport(
        element: FocusableElement,
        viewportWidth: Float,
        viewportHeight: Float,
        scrollX: Float,
        scrollY: Float
    ): Boolean {
        val viewportRight = scrollX + viewportWidth
        val viewportBottom = scrollY + viewportHeight

        return element.x < viewportRight &&
               element.x + element.width > scrollX &&
               element.y < viewportBottom &&
               element.y + element.height > scrollY
    }

    /**
     * Filter elements that are candidates for navigation in the given direction
     */
    private fun filterCandidatesByDirection(
        currentElement: FocusableElement,
        elements: List<FocusableElement>,
        direction: NavigationDirection
    ): List<FocusableElement> {
        return elements.filter { candidate ->
            when (direction) {
                NavigationDirection.UP -> {
                    // Element must be above (centerY smaller)
                    candidate.centerY < currentElement.centerY
                }
                NavigationDirection.DOWN -> {
                    // Element must be below (centerY larger)
                    candidate.centerY > currentElement.centerY
                }
                NavigationDirection.LEFT -> {
                    // Element must be to the left (centerX smaller)
                    candidate.centerX < currentElement.centerX
                }
                NavigationDirection.RIGHT -> {
                    // Element must be to the right (centerX larger)
                    candidate.centerX > currentElement.centerX
                }
            }
        }
    }

    /**
     * Find the best candidate based on spatial scoring algorithm
     */
    private fun findBestCandidate(
        currentElement: FocusableElement,
        candidates: List<FocusableElement>,
        direction: NavigationDirection
    ): FocusableElement? {
        return candidates.minByOrNull { candidate ->
            calculateScore(currentElement, candidate, direction)
        }
    }

    /**
     * Calculate navigation score for a candidate element.
     * Lower score = better candidate.
     *
     * Algorithm considers:
     * 1. Distance along primary axis (direction of navigation)
     * 2. Distance along secondary axis (perpendicular to navigation)
     * 3. Alignment with current element
     * 4. Angular distance from ideal direction
     */
    private fun calculateScore(
        current: FocusableElement,
        candidate: FocusableElement,
        direction: NavigationDirection
    ): Float {
        val dx = candidate.centerX - current.centerX
        val dy = candidate.centerY - current.centerY

        // Primary and secondary axis distances
        val (primaryDistance, secondaryDistance) = when (direction) {
            NavigationDirection.UP, NavigationDirection.DOWN -> {
                Pair(abs(dy), abs(dx))
            }
            NavigationDirection.LEFT, NavigationDirection.RIGHT -> {
                Pair(abs(dx), abs(dy))
            }
        }

        // Calculate alignment bonus (elements aligned on secondary axis are preferred)
        val alignment = calculateAlignment(current, candidate, direction)

        // Calculate angular deviation from ideal direction
        val angularDeviation = calculateAngularDeviation(dx, dy, direction)

        // Weighted score calculation
        val primaryWeight = 1.0f
        val secondaryWeight = 2.0f  // Penalize perpendicular distance more
        val alignmentWeight = 0.5f  // Bonus for alignment
        val angularWeight = 1.5f    // Penalize angular deviation

        return (primaryDistance * primaryWeight) +
               (secondaryDistance * secondaryWeight) -
               (alignment * alignmentWeight) +
               (angularDeviation * angularWeight)
    }

    /**
     * Calculate alignment between two elements
     * Returns value 0-1, where 1 = perfect alignment
     */
    private fun calculateAlignment(
        current: FocusableElement,
        candidate: FocusableElement,
        direction: NavigationDirection
    ): Float {
        return when (direction) {
            NavigationDirection.UP, NavigationDirection.DOWN -> {
                // Calculate horizontal overlap
                val currentLeft = current.x
                val currentRight = current.x + current.width
                val candidateLeft = candidate.x
                val candidateRight = candidate.x + candidate.width

                val overlapLeft = maxOf(currentLeft, candidateLeft)
                val overlapRight = minOf(currentRight, candidateRight)
                val overlap = maxOf(0f, overlapRight - overlapLeft)

                val currentWidth = current.width
                val candidateWidth = candidate.width
                val minWidth = minOf(currentWidth, candidateWidth)

                if (minWidth > 0) overlap / minWidth else 0f
            }
            NavigationDirection.LEFT, NavigationDirection.RIGHT -> {
                // Calculate vertical overlap
                val currentTop = current.y
                val currentBottom = current.y + current.height
                val candidateTop = candidate.y
                val candidateBottom = candidate.y + candidate.height

                val overlapTop = maxOf(currentTop, candidateTop)
                val overlapBottom = minOf(currentBottom, candidateBottom)
                val overlap = maxOf(0f, overlapBottom - overlapTop)

                val currentHeight = current.height
                val candidateHeight = candidate.height
                val minHeight = minOf(currentHeight, candidateHeight)

                if (minHeight > 0) overlap / minHeight else 0f
            }
        }
    }

    /**
     * Calculate angular deviation from ideal direction
     * Returns value in degrees (0-180)
     */
    private fun calculateAngularDeviation(
        dx: Float,
        dy: Float,
        direction: NavigationDirection
    ): Float {
        // Calculate angle from current to candidate
        val angleRad = atan2(dy.toDouble(), dx.toDouble())
        val angleDeg = Math.toDegrees(angleRad).toFloat()

        // Ideal angle for each direction
        val idealAngle = when (direction) {
            NavigationDirection.RIGHT -> 0f
            NavigationDirection.DOWN -> 90f
            NavigationDirection.LEFT -> 180f
            NavigationDirection.UP -> -90f
        }

        // Calculate angular difference
        var diff = abs(angleDeg - idealAngle)
        if (diff > 180f) diff = 360f - diff

        return diff
    }

    /**
     * Check if an element should be scrolled into view
     */
    fun shouldScrollToElement(
        element: FocusableElement,
        viewportWidth: Float,
        viewportHeight: Float,
        scrollX: Float,
        scrollY: Float
    ): Boolean {
        return !isInViewport(element, viewportWidth, viewportHeight, scrollX, scrollY)
    }

    /**
     * Calculate scroll offset needed to center an element in viewport
     */
    fun calculateScrollOffset(
        element: FocusableElement,
        viewportWidth: Float,
        viewportHeight: Float
    ): Pair<Float, Float> {
        val targetScrollX = element.centerX - (viewportWidth / 2)
        val targetScrollY = element.centerY - (viewportHeight / 2)
        return Pair(maxOf(0f, targetScrollX), maxOf(0f, targetScrollY))
    }
}