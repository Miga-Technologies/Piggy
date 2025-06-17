package com.miga.piggy.di

import com.miga.piggy.di.module.appModule
import com.miga.piggy.di.module.platformModule
import org.koin.core.context.startKoin

actual class KoinInitializer {
    actual fun init() {
        startKoin {
            modules(appModule, platformModule)
        }
    }
}