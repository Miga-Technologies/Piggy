package com.miga.piggy

import android.app.Application
import androidx.compose.ui.unit.DpSize
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Window
import androidx.compose.ui.window.WindowState
import androidx.compose.ui.window.application
import com.google.firebase.FirebasePlatform
import com.miga.piggy.di.module.appModule
import com.miga.piggy.di.module.platformModule
import com.miga.piggy.util.FirebaseDesktopConfig
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.FirebaseOptions
import dev.gitlive.firebase.initialize
import org.jetbrains.compose.resources.painterResource
import org.koin.compose.KoinApplication
import piggy.composeapp.generated.resources.Res
import piggy.composeapp.generated.resources.icon

fun main() = application {

    FirebasePlatform.initializeFirebasePlatform(object : FirebasePlatform() {
        val storage = mutableMapOf<String, String>()
        override fun clear(key: String) {
            storage.remove(key)
        }

        override fun log(msg: String) = println(msg)

        override fun retrieve(key: String) = storage[key]

        override fun store(key: String, value: String) = storage.set(key, value)
    })

    val options = FirebaseOptions(
        projectId = FirebaseDesktopConfig.projectId,
        applicationId = FirebaseDesktopConfig.applicationId,
        apiKey = FirebaseDesktopConfig.apiKey
    )

    Firebase.initialize(Application(), options)

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