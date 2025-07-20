package com.miga.piggy.utils

import android.app.Activity
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.provider.MediaStore
import androidx.activity.ComponentActivity
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.material3.SnackbarHostState
import androidx.compose.runtime.*
import androidx.compose.ui.platform.LocalContext
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.compose.BindEffect
import dev.icerock.moko.permissions.compose.PermissionsControllerFactory
import dev.icerock.moko.permissions.compose.rememberPermissionsControllerFactory
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import java.io.ByteArrayOutputStream
import kotlin.coroutines.resume
import com.miga.piggy.utils.permission.checkPermission

// CompositionLocal para fornecer o ImagePicker globalmente
val LocalImagePicker = compositionLocalOf<ImagePicker?> { null }

actual class ImagePicker {

    private var galleryLauncher: ActivityResultLauncher<Intent>? = null
    private var cameraLauncher: ActivityResultLauncher<Intent>? = null
    private var galleryCallback: ((ByteArray?) -> Unit)? = null
    private var cameraCallback: ((ByteArray?) -> Unit)? = null

    fun initialize(activity: ComponentActivity) {
        // Launcher para galeria
        galleryLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val callback = galleryCallback
            galleryCallback = null

            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    val imageData = uriToByteArray(activity, uri)
                    callback?.invoke(imageData)
                } ?: callback?.invoke(null)
            } else {
                callback?.invoke(null)
            }
        }

        // Launcher para câmera
        cameraLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val callback = cameraCallback
            cameraCallback = null

            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                val imageData = bitmap?.let { bitmapToByteArray(it) }
                callback?.invoke(imageData)
            } else {
                callback?.invoke(null)
            }
        }
    }

    actual suspend fun pickImageFromGallery(): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            galleryCallback = { imageData ->
                continuation.resume(imageData)
            }

            val intent = Intent(Intent.ACTION_PICK).apply {
                type = "image/*"
            }
            galleryLauncher?.launch(intent)
        }
    }

    actual suspend fun pickImageFromCamera(): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            cameraCallback = { imageData ->
                continuation.resume(imageData)
            }

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            cameraLauncher?.launch(intent)
        }
    }

    private fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            val inputStream = context.contentResolver.openInputStream(uri)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream?.close()
            bitmapToByteArray(bitmap)
        } catch (e: Exception) {
            null
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        val outputStream = ByteArrayOutputStream()
        bitmap.compress(Bitmap.CompressFormat.JPEG, 85, outputStream)
        return outputStream.toByteArray()
    }
}

@Composable
actual fun rememberImagePicker(): ImagePicker {
    // Usar o ImagePicker fornecido pelo CompositionLocal
    val imagePicker = LocalImagePicker.current

    return imagePicker
        ?: error("ImagePicker não foi inicializado. Certifique-se de que está sendo fornecido via CompositionLocalProvider.")
}

@Composable
actual fun ImagePickerWithPermissions(
    onImageSelected: (ByteArray?) -> Unit,
    onPermissionDenied: () -> Unit,
    content: @Composable (pickFromGallery: () -> Unit, pickFromCamera: () -> Unit) -> Unit
) {
    val imagePicker = rememberImagePicker()
    val scope = rememberCoroutineScope()
    val factory: PermissionsControllerFactory = rememberPermissionsControllerFactory()
    val controller = remember(factory) { factory.createPermissionsController() }
    val snackbarHostState = remember { SnackbarHostState() }

    BindEffect(controller)

    content(
        { // pickFromGallery
            scope.launch {
                try {
                    // Para galeria não precisa de permissão especial no Android moderno
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
                    // Verificar permissão usando moko-permissions
                    val hasPermission = checkPermission(
                        permission = Permission.CAMERA,
                        controller = controller,
                        snackBarHostState = snackbarHostState
                    )

                    if (hasPermission) {
                        val imageData = imagePicker.pickImageFromCamera()
                        onImageSelected(imageData)
                    } else {
                        onPermissionDenied()
                    }
                } catch (e: Exception) {
                    onPermissionDenied()
                    onImageSelected(null)
                }
            }
        }
    )
}