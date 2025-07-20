package com.miga.piggy.utils.ui

import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.toComposeImageBitmap
import com.miga.piggy.utils.Base64Utils
import kotlinx.cinterop.*
import org.jetbrains.skia.Image
import platform.Foundation.NSData
import platform.Foundation.create

@OptIn(ExperimentalForeignApi::class)
actual fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    return try {
        println("DEBUG iOS: Converting base64 string to image bitmap, length: ${base64String.length}")

        // Decodificar diretamente o string base64
        val imageBytes = Base64Utils.decode(base64String)
        println("DEBUG iOS: Decoded bytes size: ${imageBytes.size}")

        // Usar Skia para decodificar a imagem
        val skiaImage = Image.makeFromEncoded(imageBytes)
        val result = skiaImage?.toComposeImageBitmap()

        if (result != null) {
            println("DEBUG iOS: Successfully converted to ImageBitmap")
        } else {
            println("DEBUG iOS: Failed to convert to ImageBitmap")
        }

        result
    } catch (e: Exception) {
        println("DEBUG iOS: Exception in base64ToImageBitmap: ${e.message}")
        null
    }
}