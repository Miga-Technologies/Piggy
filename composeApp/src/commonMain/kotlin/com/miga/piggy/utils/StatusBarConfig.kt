package com.miga.piggy.utils

/**
 * Platform-specific status bar controller
 * Handles status bar appearance based on theme changes
 */
expect class StatusBarController() {
    /**
     * Sets the status bar style based on the current theme
     * @param isDarkTheme true if dark theme is active, false for light theme
     */
    fun setStatusBarStyle(isDarkTheme: Boolean)
}