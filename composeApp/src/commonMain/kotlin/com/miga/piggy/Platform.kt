package com.miga.piggy

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform