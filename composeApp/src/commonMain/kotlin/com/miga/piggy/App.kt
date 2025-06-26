package com.miga.piggy

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.miga.piggy.splash.SplashScreen
import org.koin.compose.KoinContext

@Composable
fun App() {
    KoinContext {
        MaterialTheme {
            Navigator(SplashScreen) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}