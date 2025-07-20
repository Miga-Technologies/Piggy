package com.miga.piggy

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.Modifier
import androidx.compose.ui.window.ComposeUIViewController
import com.miga.piggy.di.KoinInitializer
import com.miga.piggy.utils.ImagePicker
import com.miga.piggy.utils.LocalImagePicker
import platform.UIKit.UIViewController

fun MainViewController(): UIViewController =
    ComposeUIViewController(
        configure = {
            KoinInitializer().init()
        }
    ) {
        val imagePicker = ImagePicker().apply { initialize() }

        Surface(
            modifier = Modifier.fillMaxSize(),
            color = MaterialTheme.colorScheme.background
        ) {
            CompositionLocalProvider(LocalImagePicker provides imagePicker) {
                App()
            }
        }
    }