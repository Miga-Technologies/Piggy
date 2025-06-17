package com.miga.piggy

import androidx.compose.ui.window.ComposeUIViewController
import com.miga.piggy.di.KoinInitializer

fun MainViewController() =
    ComposeUIViewController(
        configure = {
            KoinInitializer().init()
        }
    ) {
        App()
    }