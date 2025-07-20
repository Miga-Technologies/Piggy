package com.miga.piggy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
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
            CompositionLocalProvider(LocalImagePicker provides imagePicker) {
                App()
            }
        }
    }
}