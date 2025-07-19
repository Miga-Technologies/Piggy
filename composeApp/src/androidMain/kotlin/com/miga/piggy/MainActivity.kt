package com.miga.piggy

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import com.google.firebase.FirebaseApp

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        // Switch from splash theme to app theme
        setTheme(R.style.AppTheme)

        enableEdgeToEdge()
        super.onCreate(savedInstanceState)
        FirebaseApp.initializeApp(this)
        setContent {
            App()
        }
    }
}