package com.miga.piggy.utils

import android.util.Base64

actual object Base64Utils {
    actual fun encodeToString(input: ByteArray): String {
        return Base64.encodeToString(input, Base64.NO_WRAP)
    }

    actual fun decode(input: String): ByteArray {
        return Base64.decode(input, Base64.NO_WRAP)
    }
}