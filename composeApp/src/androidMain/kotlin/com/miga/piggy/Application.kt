package com.miga.piggy

import android.app.Application
import com.miga.piggy.di.KoinInitializer

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
    }
}