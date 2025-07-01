package com.miga.piggy

import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.miga.piggy.di.module.appModule
import com.miga.piggy.di.module.platformModule
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinApplication
import piggy.composeapp.generated.resources.Res
import piggy.composeapp.generated.resources.icon

fun main() = application {
    Window(
        onCloseRequest = ::exitApplication,
        title = "Piggy",
        state = WindowState(
            size = DpSize(width = 1024.dp, height = 768.dp)
        ),
        resizable = true,
        onKeyEvent = { false },
        icon = painterResource(Res.drawable.icon)
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