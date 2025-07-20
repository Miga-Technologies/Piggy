package com.miga.piggy.utils

import androidx.compose.runtime.*
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

actual class ImagePicker {

    actual suspend fun pickImageFromGallery(): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            // TODO: Implementar UIImagePickerController para galeria
            // Por enquanto retorna dados mockados para teste
            continuation.resume(createMockImageData())
        }
    }

    actual suspend fun pickImageFromCamera(): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            // TODO: Implementar UIImagePickerController para câmera
            // Por enquanto retorna dados mockados para teste
            continuation.resume(createMockImageData())
        }
    }

    private fun createMockImageData(): ByteArray {
        // Criar uma imagem simples de 1x1 pixel em JPEG
        // Isso é apenas para teste - em produção seria a imagem real
        return byteArrayOf(
            0xFF.toByte(), 0xD8.toByte(), 0xFF.toByte(), 0xE0.toByte(),
            0x00.toByte(), 0x10.toByte(), 0x4A.toByte(), 0x46.toByte(),
            0x49.toByte(), 0x46.toByte(), 0x00.toByte(), 0x01.toByte(),
            0xFF.toByte(), 0xD9.toByte()
        )
    }
}

@Composable
actual fun rememberImagePicker(): ImagePicker {
    return remember { ImagePicker() }
}

@Composable
actual fun ImagePickerWithPermissions(
    onImageSelected: (ByteArray?) -> Unit,
    onPermissionDenied: () -> Unit,
    content: @Composable (pickFromGallery: () -> Unit, pickFromCamera: () -> Unit) -> Unit
) {
    val imagePicker = rememberImagePicker()
    val scope = rememberCoroutineScope()
    
    content(
        { // pickFromGallery
            scope.launch {
                try {
                    val imageData = imagePicker.pickImageFromGallery()
                    onImageSelected(imageData)
                } catch (e: Exception) {
                    onImageSelected(null)
                }
            }
        },
        { // pickFromCamera
            scope.launch {
                try {
                    // No iOS, seria necessário verificar permissão da câmera
                    val imageData = imagePicker.pickImageFromCamera()
                    onImageSelected(imageData)
                } catch (e: Exception) {
                    onPermissionDenied()
                    onImageSelected(null)
                }
            }
        }
    )
}