package com.miga.piggy.utils

import androidx.compose.runtime.Composable

expect class ImagePicker {
    suspend fun pickImageFromGallery(): ByteArray?
    suspend fun pickImageFromCamera(): ByteArray?
}

@Composable
expect fun rememberImagePicker(): ImagePicker

@Composable
expect fun ImagePickerWithPermissions(
    onImageSelected: (ByteArray?) -> Unit,
    onPermissionDenied: () -> Unit = {},
    content: @Composable (pickFromGallery: () -> Unit, pickFromCamera: () -> Unit) -> Unit
)

// Interface comum para o resultado do picker
sealed class ImagePickerResult {
    data class Success(val data: ByteArray) : ImagePickerResult()
    object Cancelled : ImagePickerResult()
    data class Error(val exception: Throwable) : ImagePickerResult()
}