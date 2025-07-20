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

            println("DEBUG: Gallery result received - resultCode: ${result.resultCode}, data: ${result.data}") // Debug log
            if (result.resultCode == Activity.RESULT_OK) {
                result.data?.data?.let { uri ->
                    println("DEBUG: Gallery URI received: $uri") // Debug log
                    val imageData = uriToByteArray(activity, uri)
                    println("DEBUG: Converted image data size: ${imageData?.size ?: 0}") // Debug log
                    callback?.invoke(imageData)
                } ?: run {
                    println("DEBUG: Gallery result data or URI is null") // Debug log
                    callback?.invoke(null)
                }
            } else {
                println("DEBUG: Gallery result cancelled or failed") // Debug log
                callback?.invoke(null)
            }
        }

        // Launcher para câmera
        cameraLauncher = activity.registerForActivityResult(
            ActivityResultContracts.StartActivityForResult()
        ) { result ->
            val callback = cameraCallback
            cameraCallback = null

            println("DEBUG: Camera result received - resultCode: ${result.resultCode}, data: ${result.data}") // Debug log
            if (result.resultCode == Activity.RESULT_OK) {
                val bitmap = result.data?.extras?.get("data") as? Bitmap
                if (bitmap != null) {
                    println("DEBUG: Camera bitmap received - width: ${bitmap.width}, height: ${bitmap.height}") // Debug log
                    val imageData = bitmapToByteArray(bitmap)
                    println("DEBUG: Camera image converted to byte array - size: ${imageData.size}") // Debug log
                    callback?.invoke(imageData)
                } else {
                    println("DEBUG: Camera bitmap is null") // Debug log
                    callback?.invoke(null)
                }
            } else {
                println("DEBUG: Camera result cancelled or failed") // Debug log
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
            println("DEBUG: Starting camera picker") // Debug log
            cameraCallback = { imageData ->
                println("DEBUG: Camera callback invoked with data size: ${imageData?.size ?: 0}") // Debug log
                continuation.resume(imageData)
            }

            val intent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
            println("DEBUG: Camera intent created: $intent") // Debug log

            // Verificar se há apps que podem lidar com o intent
            val packageManager =
                cameraLauncher?.let { launcher ->
                    // Tentar obter o PackageManager do contexto
                    null // Por enquanto, vamos continuar sem verificação
                }

            try {
                println("DEBUG: Launching camera intent") // Debug log
                cameraLauncher?.launch(intent)
            } catch (e: Exception) {
                println("DEBUG: Exception launching camera intent: ${e.message}") // Debug log
                continuation.resume(null)
            }
        }
    }

    private fun uriToByteArray(context: Context, uri: Uri): ByteArray? {
        return try {
            println("DEBUG: Starting uriToByteArray for URI: $uri") // Debug log
            val inputStream = context.contentResolver.openInputStream(uri)
            if (inputStream == null) {
                println("DEBUG: Failed to open input stream for URI: $uri") // Debug log
                return null
            }

            println("DEBUG: Input stream opened successfully") // Debug log
            val bitmap = BitmapFactory.decodeStream(inputStream)
            inputStream.close()

            if (bitmap == null) {
                println("DEBUG: Failed to decode bitmap from stream") // Debug log
                return null
            }

            println("DEBUG: Bitmap decoded successfully - width: ${bitmap.width}, height: ${bitmap.height}") // Debug log
            val result = bitmapToByteArray(bitmap)
            println("DEBUG: Final byte array size: ${result.size}") // Debug log
            result
        } catch (e: Exception) {
            println("DEBUG: Exception in uriToByteArray: ${e.message}") // Debug log
            e.printStackTrace()
            null
        }
    }

    private fun bitmapToByteArray(bitmap: Bitmap): ByteArray {
        // Redimensionar a imagem se for muito grande
        val maxSize = 800
        val resizedBitmap = if (bitmap.width > maxSize || bitmap.height > maxSize) {
            resizeBitmap(bitmap, maxSize)
        } else {
            bitmap
        }

        val stream = ByteArrayOutputStream()
        // Usar compressão mais agressiva (30% quality)
        resizedBitmap.compress(Bitmap.CompressFormat.JPEG, 30, stream)
        val byteArray = stream.toByteArray()

        println("DEBUG: Original bitmap size: ${bitmap.width}x${bitmap.height}")
        println("DEBUG: Resized bitmap size: ${resizedBitmap.width}x${resizedBitmap.height}")
        println("DEBUG: Compressed byte array size: ${byteArray.size} bytes")

        // Limpar recursos se criamos uma nova bitmap
        if (resizedBitmap != bitmap) {
            resizedBitmap.recycle()
        }

        return byteArray
    }

    private fun resizeBitmap(bitmap: Bitmap, maxSize: Int): Bitmap {
        val width = bitmap.width
        val height = bitmap.height

        val ratio = minOf(maxSize.toFloat() / width, maxSize.toFloat() / height)
        val newWidth = (width * ratio).toInt()
        val newHeight = (height * ratio).toInt()

        return Bitmap.createScaledBitmap(bitmap, newWidth, newHeight, true)
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
                    println("DEBUG: Starting gallery picker coroutine") // Debug log
                    // Para galeria não precisa de permissão especial no Android moderno
                    val imageData = imagePicker.pickImageFromGallery()
                    println("DEBUG: Gallery picker returned data size: ${imageData?.size ?: 0}") // Debug log
                    onImageSelected(imageData)
                } catch (e: Exception) {
                    println("DEBUG: Exception in gallery picker: ${e.message}") // Debug log
                    onImageSelected(null)
                }
            }
        },
        { // pickFromCamera  
            scope.launch {
                try {
                    println("DEBUG: Starting camera permission check") // Debug log
                    // Verificar permissão usando moko-permissions
                    val hasPermission = checkPermission(
                        permission = Permission.CAMERA,
                        controller = controller,
                        snackBarHostState = snackbarHostState
                    )

                    println("DEBUG: Camera permission result: $hasPermission") // Debug log
                    if (hasPermission) {
                        println("DEBUG: Camera permission granted, starting camera picker") // Debug log
                        val imageData = imagePicker.pickImageFromCamera()
                        println("DEBUG: Camera picker finished with data size: ${imageData?.size ?: 0}") // Debug log
                        onImageSelected(imageData)
                    } else {
                        println("DEBUG: Camera permission denied") // Debug log
                        onPermissionDenied()
                    }
                } catch (e: Exception) {
                    println("DEBUG: Exception in camera picker flow: ${e.message}") // Debug log
                    e.printStackTrace()
                    onPermissionDenied()
                    onImageSelected(null)
                }
            }
        }
    )
}