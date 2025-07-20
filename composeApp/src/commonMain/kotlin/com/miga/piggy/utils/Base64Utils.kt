package com.miga.piggy.utils

expect object Base64Utils {
    fun encodeToString(input: ByteArray): String
    fun decode(input: String): ByteArray
}