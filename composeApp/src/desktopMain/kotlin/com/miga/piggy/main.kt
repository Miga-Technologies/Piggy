package com.miga.piggy

import androidx.compose.ui.window.Window
import androidx.compose.ui.window.application
import com.miga.piggy.di.module.appModule
import com.miga.piggy.di.module.platformModule
import org.koin.compose.KoinApplication

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Piggy",
    ) {
        KoinApplication(
            application = {
                modules(appModule, platformModule)
            }
        ) {
            App()
        }
    }
}