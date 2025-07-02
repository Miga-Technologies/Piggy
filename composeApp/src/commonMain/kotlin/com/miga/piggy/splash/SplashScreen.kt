package com.miga.piggy.splash

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.miga.piggy.auth.presentation.ui.AuthScreen
import com.miga.piggy.auth.presentation.viewmodel.AuthViewModel
import com.miga.piggy.home.presentation.ui.HomeScreen
import org.koin.compose.koinInject

object SplashScreen : Screen {
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AuthViewModel = koinInject()
        val uiState by viewModel.uiState.collectAsState()

        LaunchedEffect(Unit) {
            viewModel.checkIfUserIsLoggedAndVerified()
        }

        LaunchedEffect(uiState.user, uiState.isEmailVerified) {
            if (uiState.isEmailVerified == null) return@LaunchedEffect

            if (uiState.user != null && uiState.isEmailVerified == true) {
                navigator.replaceAll(HomeScreen)
            } else {
                navigator.replaceAll(AuthScreen)
            }
        }
    }
}