package com.miga.piggy

import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.darkColorScheme
import androidx.compose.material3.lightColorScheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.miga.piggy.splash.SplashScreen
import org.koin.compose.KoinContext

// Simple preference storage - could be replaced with proper SharedPreferences implementation
object PreferenceStorage {
    private val preferences = mutableMapOf<String, Any>()

    fun putBoolean(key: String, value: Boolean) {
        preferences[key] = value
    }

    fun getBoolean(key: String, defaultValue: Boolean): Boolean {
        return preferences[key] as? Boolean ?: defaultValue
    }

    fun putString(key: String, value: String) {
        preferences[key] = value
    }

    fun getString(key: String, defaultValue: String): String {
        return preferences[key] as? String ?: defaultValue
    }
}

// Global theme state
object ThemeManager {
    private const val THEME_PREFERENCE_KEY = "is_dark_theme"
    private const val THEME_SET_BY_USER_KEY = "theme_set_by_user"

    private val _isDarkTheme = mutableStateOf<Boolean?>(null)
    val isDarkTheme: State<Boolean?> = _isDarkTheme

    private var _systemInDarkTheme = false
    private var _initialized = false

    fun initSystemTheme(systemInDarkTheme: Boolean) {
        _systemInDarkTheme = systemInDarkTheme

        if (!_initialized) {
            _initialized = true
            // Load saved preference
            val isThemeSetByUser = PreferenceStorage.getBoolean(THEME_SET_BY_USER_KEY, false)
            if (isThemeSetByUser) {
                val savedTheme =
                    PreferenceStorage.getBoolean(THEME_PREFERENCE_KEY, systemInDarkTheme)
                _isDarkTheme.value = savedTheme
            } else {
                // First time, use system theme but don't save it yet
                _isDarkTheme.value = systemInDarkTheme
            }
        }
    }

    fun setDarkTheme(isDark: Boolean) {
        _isDarkTheme.value = isDark
        // Save preference
        PreferenceStorage.putBoolean(THEME_PREFERENCE_KEY, isDark)
        PreferenceStorage.putBoolean(THEME_SET_BY_USER_KEY, true)
    }

    fun toggleTheme() {
        val newTheme = !(_isDarkTheme.value ?: _systemInDarkTheme)
        setDarkTheme(newTheme)
    }

    fun getCurrentTheme(): Boolean {
        return _isDarkTheme.value ?: _systemInDarkTheme
    }
}

@Composable
fun App() {
    KoinContext {
        val systemInDarkTheme = isSystemInDarkTheme()

        LaunchedEffect(systemInDarkTheme) {
            ThemeManager.initSystemTheme(systemInDarkTheme)
        }

        val isDarkTheme by ThemeManager.isDarkTheme
        val useDarkTheme = isDarkTheme ?: systemInDarkTheme

        MaterialTheme(
            colorScheme = if (useDarkTheme) darkColorScheme() else lightColorScheme()
        ) {
            // Garantir que o background cubra toda a tela
            androidx.compose.material3.Surface(
                modifier = androidx.compose.ui.Modifier.fillMaxSize(),
                color = MaterialTheme.colorScheme.background
            ) {
                Navigator(SplashScreen) { navigator ->
                    SlideTransition(navigator)
                }
            }
        }
    }
}