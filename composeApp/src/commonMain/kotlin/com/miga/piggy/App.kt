package com.miga.piggy

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.*
import cafe.adriel.voyager.navigator.Navigator
import cafe.adriel.voyager.transitions.SlideTransition
import com.miga.piggy.auth.presentation.ui.AuthScreen
import org.jetbrains.compose.ui.tooling.preview.Preview
import org.koin.compose.KoinContext

@Composable
@Preview
fun App() {
    KoinContext {
        MaterialTheme {
            Navigator(AuthScreen()) { navigator ->
                SlideTransition(navigator)
            }
        }
    }
}