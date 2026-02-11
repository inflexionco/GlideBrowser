/**
 * Glide Browser - Navigation JavaScript Injection
 * Detects and reports focusable elements on the page for D-pad navigation
 */

(function() {
    'use strict';

    // Check if already injected
    if (window.GlideBrowserInjected) {
        return;
    }
    window.GlideBrowserInjected = true;

    // Console override to capture logs
    const originalConsoleLog = console.log;
    console.log = function(...args) {
        originalConsoleLog.apply(console, args);
        try {
            if (typeof GlideAndroid !== 'undefined') {
                GlideAndroid.postConsoleLog(args.join(' '));
            }
        } catch (e) {
            originalConsoleLog('Glide logging error:', e);
        }
    };

    /**
     * Check if element is visible
     */
    function isElementVisible(element) {
        if (!element) return false;

        const style = window.getComputedStyle(element);
        if (style.display === 'none' || style.visibility === 'hidden' || style.opacity === '0') {
            return false;
        }

        const rect = element.getBoundingClientRect();
        if (rect.width === 0 || rect.height === 0) {
            return false;
        }

        return true;
    }

    /**
     * Get element type classification
     */
    function getElementType(element) {
        const tagName = element.tagName.toLowerCase();

        if (tagName === 'a') return 'link';
        if (tagName === 'button') return 'button';
        if (tagName === 'input') {
            const type = element.getAttribute('type') || 'text';
            return 'input_' + type;
        }
        if (tagName === 'select') return 'select';
        if (tagName === 'textarea') return 'textarea';
        if (element.getAttribute('role') === 'button') return 'button';
        if (element.onclick || element.getAttribute('onclick')) return 'clickable';
        if (tagName === 'label') return 'label';

        return 'other';
    }

    /**
     * Get visible text content
     */
    function getElementText(element) {
        let text = element.getAttribute('aria-label') ||
                   element.getAttribute('title') ||
                   element.getAttribute('alt') ||
                   element.innerText ||
                   element.textContent ||
                   '';
        return text.trim().substring(0, 100); // Limit text length
    }

    /**
     * Generate unique ID for element
     */
    function generateElementId(element, index) {
        return element.id ||
               element.getAttribute('data-glide-id') ||
               'glide_element_' + index;
    }

    /**
     * Find all focusable elements on the page
     */
    function detectFocusableElements() {
        const selectors = [
            'a[href]',
            'button:not([disabled])',
            'input:not([disabled]):not([type="hidden"])',
            'select:not([disabled])',
            'textarea:not([disabled])',
            '[role="button"]',
            '[role="link"]',
            '[onclick]',
            '[tabindex]:not([tabindex="-1"])'
        ];

        const elements = document.querySelectorAll(selectors.join(','));
        const focusableElements = [];
        let elementIndex = 0;

        elements.forEach((element) => {
            if (!isElementVisible(element)) {
                return;
            }

            const rect = element.getBoundingClientRect();
            const elementData = {
                id: generateElementId(element, elementIndex++),
                tagName: element.tagName.toLowerCase(),
                type: getElementType(element),
                text: getElementText(element),
                href: element.href || null,
                x: rect.left + window.scrollX,
                y: rect.top + window.scrollY,
                width: rect.width,
                height: rect.height,
                isVisible: true,
                className: element.className || null,
                ariaLabel: element.getAttribute('aria-label') || null
            };

            // Store reference for later interaction
            if (!element.getAttribute('data-glide-id')) {
                element.setAttribute('data-glide-id', elementData.id);
            }

            focusableElements.push(elementData);
        });

        return focusableElements;
    }

    /**
     * Click element by ID
     */
    function clickElement(elementId) {
        const element = document.querySelector('[data-glide-id="' + elementId + '"]') ||
                       document.getElementById(elementId);

        if (element) {
            element.click();
            element.focus();
            return true;
        }
        return false;
    }

    /**
     * Scroll element into view
     */
    function scrollToElement(elementId) {
        const element = document.querySelector('[data-glide-id="' + elementId + '"]') ||
                       document.getElementById(elementId);

        if (element) {
            element.scrollIntoView({ behavior: 'smooth', block: 'center' });
            return true;
        }
        return false;
    }

    /**
     * Send detected elements to Android
     */
    function sendElementsToAndroid() {
        try {
            const elements = detectFocusableElements();
            const jsonData = JSON.stringify(elements);

            if (typeof GlideAndroid !== 'undefined') {
                GlideAndroid.postElementsData(jsonData);
                console.log('Glide: Detected ' + elements.length + ' focusable elements');
            }
        } catch (e) {
            console.error('Glide detection error:', e);
        }
    }

    /**
     * Monitor scroll events
     */
    let scrollTimeout;
    window.addEventListener('scroll', function() {
        clearTimeout(scrollTimeout);
        scrollTimeout = setTimeout(function() {
            if (typeof GlideAndroid !== 'undefined') {
                GlideAndroid.postPageScrolled(window.scrollX, window.scrollY);
            }
        }, 100);
    }, { passive: true });

    // Expose API to Android
    window.GlideBrowser = {
        detectElements: sendElementsToAndroid,
        clickElement: clickElement,
        scrollToElement: scrollToElement,
        version: '1.0.0'
    };

    // Initial detection after page load
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', sendElementsToAndroid);
    } else {
        // Page already loaded
        setTimeout(sendElementsToAndroid, 500);
    }

    // Re-detect on major DOM changes
    const observer = new MutationObserver(function(mutations) {
        if (mutations.length > 5) { // Only if significant changes
            clearTimeout(window.glideDetectionTimeout);
            window.glideDetectionTimeout = setTimeout(sendElementsToAndroid, 1000);
        }
    });

    observer.observe(document.body || document.documentElement, {
        childList: true,
        subtree: true,
        attributes: false
    });

    console.log('Glide Browser navigation injection complete');
})();