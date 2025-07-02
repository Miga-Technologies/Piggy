package com.miga.piggy

import android.app.Application
import com.miga.piggy.di.KoinInitializer
import dev.gitlive.firebase.Firebase
import dev.gitlive.firebase.initialize

class Application : Application() {
    override fun onCreate() {
        super.onCreate()
        KoinInitializer(applicationContext).init()
        Firebase.initialize(applicationContext)
    }
}