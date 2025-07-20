package com.miga.piggy.utils

actual class StatusBarController actual constructor() {
    actual fun setStatusBarStyle(isDarkTheme: Boolean) {
        // iOS status bar configuration
        // Note: This requires proper setup in Info.plist to control status bar appearance
        // For now, we'll rely on the system to handle it automatically

        // The actual status bar configuration in iOS should be done at the app level
        // through UIViewController preferredStatusBarStyle override or Info.plist settings
    }
}