package com.miga.piggy.util

import java.util.Properties

object FirebaseDesktopConfig {
    private val props: Properties by lazy {
        Properties().apply {
            val inputStream = object {}.javaClass.classLoader.getResourceAsStream("firebase.properties")
                ?: error("Arquivo firebase.properties não encontrado no classpath")
            load(inputStream)
        }
    }

    val projectId: String get() = props["FIREBASE_PROJECT_ID"] as String
    val applicationId: String get() = props["FIREBASE_APPLICATION_ID"] as String
    val apiKey: String get() = props["FIREBASE_API_KEY"] as String
}
