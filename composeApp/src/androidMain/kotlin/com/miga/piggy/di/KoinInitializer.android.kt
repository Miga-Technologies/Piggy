package com.miga.piggy.di

import android.content.Context
import com.miga.piggy.di.module.appModule
import com.miga.piggy.di.module.platformModule
import org.koin.android.ext.koin.androidContext
import org.koin.android.ext.koin.androidLogger
import org.koin.core.context.startKoin

actual class KoinInitializer(private val context: Context) {
    actual fun init() {
        startKoin {
            androidContext(context)
            androidLogger()
            modules(appModule, platformModule)
        }
    }
}