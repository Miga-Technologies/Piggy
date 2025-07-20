package com.miga.piggy.utils

import kotlinx.cinterop.*
import platform.Foundation.NSData
import platform.Foundation.base64EncodedStringWithOptions
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class, BetaInteropApi::class)
actual object Base64Utils {
    actual fun encodeToString(input: ByteArray): String {
        val nsData = input.usePinned { pinned ->
            NSData.create(bytes = pinned.addressOf(0), length = input.size.toULong())
        }
        return nsData.base64EncodedStringWithOptions(0u)
    }

    actual fun decode(input: String): ByteArray {
        return try {
            // Tentar decodificar como base64
            val base64Data = NSData.create(base64EncodedString = input, options = 0u)
            
            base64Data?.let { data ->
                val bytes = ByteArray(data.length.toInt())
                bytes.usePinned { pinned ->
                    platform.posix.memcpy(pinned.addressOf(0), data.bytes, data.length.toULong())
                }
                bytes
            } ?: ByteArray(0)
        } catch (e: Exception) {
            println("DEBUG iOS: Error decoding base64: ${e.message}")
            ByteArray(0)
        }
    }
}