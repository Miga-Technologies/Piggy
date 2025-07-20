package com.miga.piggy.utils.ui

import android.graphics.BitmapFactory
import androidx.compose.ui.graphics.ImageBitmap
import androidx.compose.ui.graphics.asImageBitmap
import com.miga.piggy.utils.Base64Utils

actual fun base64ToImageBitmap(base64String: String): ImageBitmap? {
    return try {
        val imageBytes = Base64Utils.decode(base64String)
        val bitmap = BitmapFactory.decodeByteArray(imageBytes, 0, imageBytes.size)
        bitmap?.asImageBitmap()
    } catch (e: Exception) {
        null
    }
}