package com.miga.piggy.utils.permission

actual fun isDesktopPlatform(): Boolean {
    return try {
        val osName = System.getProperty("os.name")?.lowercase() ?: ""
        osName.contains("windows") || osName.contains("mac") || osName.contains("linux")
    } catch (e: Exception) {
        false
    }
}