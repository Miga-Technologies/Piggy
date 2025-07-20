package com.miga.piggy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalView
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.firebase.FirebaseApp
import com.miga.piggy.utils.ImagePicker
import com.miga.piggy.utils.LocalImagePicker
import androidx.compose.runtime.CompositionLocalProvider

class MainActivity : ComponentActivity() {

    private lateinit var imagePicker: ImagePicker

    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch from splash theme to app theme
        setTheme(R.style.AppTheme)

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)

        // Inicializar ImagePicker antes de setContent
        imagePicker = ImagePicker().apply {
            initialize(this@MainActivity)
        }

        setContent {
            val view = LocalView.current
            val systemInDarkTheme = isSystemInDarkTheme()
            val isDarkTheme by ThemeManager.isDarkTheme
            val useDarkTheme = isDarkTheme ?: systemInDarkTheme

            // Configure status bar based on theme
            LaunchedEffect(useDarkTheme) {
                val window = window
                val insetsController = WindowCompat.getInsetsController(window, view)

                // Set status bar appearance - light icons for dark theme, dark icons for light theme
                insetsController.isAppearanceLightStatusBars = !useDarkTheme

                // Set navigation bar appearance  
                insetsController.isAppearanceLightNavigationBars = !useDarkTheme
            }

            CompositionLocalProvider(LocalImagePicker provides imagePicker) {
                App()
            }
        }
    }
}