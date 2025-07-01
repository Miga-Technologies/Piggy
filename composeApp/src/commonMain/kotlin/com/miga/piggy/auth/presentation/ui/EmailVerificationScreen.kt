package com.miga.piggy.auth.presentation.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import cafe.adriel.voyager.core.screen.Screen
import cafe.adriel.voyager.navigator.LocalNavigator
import cafe.adriel.voyager.navigator.currentOrThrow
import com.miga.piggy.auth.presentation.viewmodel.AuthViewModel
import com.miga.piggy.utils.composables.FixedSizeWrapper
import org.koin.compose.koinInject

object EmailVerificationScreen : Screen {
    @OptIn(ExperimentalMaterial3Api::class)
    @Composable
    override fun Content() {
        val navigator = LocalNavigator.currentOrThrow
        val viewModel: AuthViewModel = koinInject()
        val uiState by viewModel.uiState.collectAsState()

        FixedSizeWrapper(
            maxWidth = 800.dp,
            maxHeight = 600.dp
        ) {
            Scaffold(
                topBar = {
                    TopAppBar(
                        title = { Text("Verificação de E-mail") }
                    )
                }
            ) { padding ->
                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding),
                    horizontalAlignment = Alignment.CenterHorizontally,
                    verticalArrangement = Arrangement.Center
                ) {
                    Text(
                        text = "Por favor, verifique sua caixa de entrada (e spam!) e clique no link para ativar sua conta.",
                        textAlign = TextAlign.Center,
                        modifier = Modifier.padding(16.dp)
                    )

                    if (uiState.verificationEmailSent) {
                        Text(
                            text = "Um novo e-mail de verificação foi enviado.",
                            color = MaterialTheme.colorScheme.secondary,
                            modifier = Modifier.padding(8.dp)
                        )
                    }

                    Spacer(modifier = Modifier.height(24.dp))

                    Button(
                        onClick = {
                            viewModel.resendVerificationEmail()
                        }
                    ) {
                        Text("Reenviar E-mail")
                    }

                    Spacer(modifier = Modifier.height(16.dp))

                    Button(
                        onClick = {
                            viewModel.checkEmailVerificationStatus()
                        }
                    ) {
                        Text("Já verifiquei meu e-mail")
                    }

                    LaunchedEffect(uiState.isEmailVerified) {
                        if (uiState.isEmailVerified == true) {
                            navigator.replaceAll(AuthScreen)
                        }
                    }
                }
            }
        }
    }
}