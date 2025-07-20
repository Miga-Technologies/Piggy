package com.miga.piggy.utils

import androidx.compose.runtime.*
import kotlinx.cinterop.*
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import platform.AVFoundation.AVAuthorizationStatusAuthorized
import platform.AVFoundation.AVAuthorizationStatusDenied
import platform.AVFoundation.AVAuthorizationStatusNotDetermined
import platform.AVFoundation.AVAuthorizationStatusRestricted
import platform.AVFoundation.AVCaptureDevice
import platform.AVFoundation.AVMediaTypeVideo
import platform.AVFoundation.authorizationStatusForMediaType
import platform.AVFoundation.requestAccessForMediaType
import platform.CoreGraphics.CGRectMake
import platform.CoreGraphics.CGSizeMake
import platform.Foundation.NSData
import platform.Photos.PHAuthorizationStatusAuthorized
import platform.Photos.PHAuthorizationStatusDenied
import platform.Photos.PHAuthorizationStatusNotDetermined
import platform.Photos.PHAuthorizationStatusRestricted
import platform.Photos.PHPhotoLibrary
import platform.UIKit.*
import platform.darwin.NSObject
import platform.darwin.dispatch_async
import platform.darwin.dispatch_get_main_queue
import platform.posix.memcpy
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

// CompositionLocal para iOS (similar ao Android)
val LocalImagePicker = compositionLocalOf<ImagePicker?> { null }

actual class ImagePicker {
    
    private var currentContinuation: CancellableContinuation<ByteArray?>? = null
    private var pickerDelegate: ImagePickerDelegate? = null

    fun initialize() {
        // No iOS, inicialização simples
    }

    actual suspend fun pickImageFromGallery(): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            currentContinuation = continuation
            
            // Verificar permissão da galeria primeiro
            val authStatus = PHPhotoLibrary.authorizationStatus()
            when (authStatus) {
                PHAuthorizationStatusAuthorized -> {
                    // Executar na main thread
                    dispatch_async(dispatch_get_main_queue()) {
                        presentGalleryPicker()
                    }
                }
                PHAuthorizationStatusNotDetermined -> {
                    PHPhotoLibrary.requestAuthorization { status ->
                        if (status == PHAuthorizationStatusAuthorized) {
                            // Executar na main thread
                            dispatch_async(dispatch_get_main_queue()) {
                                presentGalleryPicker()
                            }
                        } else {
                            continuation.resume(null)
                        }
                    }
                }
                PHAuthorizationStatusDenied, PHAuthorizationStatusRestricted -> {
                    continuation.resume(null)
                }
                else -> {
                    continuation.resume(null)
                }
            }
        }
    }

    actual suspend fun pickImageFromCamera(): ByteArray? {
        return suspendCancellableCoroutine { continuation ->
            currentContinuation = continuation
            
            // Verificar se a câmera está disponível
            if (!UIImagePickerController.isSourceTypeAvailable(UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera)) {
                continuation.resume(null)
                return@suspendCancellableCoroutine
            }
            
            // Verificar permissão da câmera
            val authStatus = AVCaptureDevice.authorizationStatusForMediaType(AVMediaTypeVideo)
            when (authStatus) {
                AVAuthorizationStatusAuthorized -> {
                    // Executar na main thread
                    dispatch_async(dispatch_get_main_queue()) {
                        presentCameraPicker()
                    }
                }
                AVAuthorizationStatusNotDetermined -> {
                    AVCaptureDevice.requestAccessForMediaType(AVMediaTypeVideo) { granted ->
                        if (granted) {
                            // Executar na main thread
                            dispatch_async(dispatch_get_main_queue()) {
                                presentCameraPicker()
                            }
                        } else {
                            continuation.resume(null)
                        }
                    }
                }
                AVAuthorizationStatusDenied, AVAuthorizationStatusRestricted -> {
                    continuation.resume(null)
                }
                else -> {
                    continuation.resume(null)
                }
            }
        }
    }
    
    private fun presentGalleryPicker() {
        val picker = UIImagePickerController()
        picker.sourceType =
            UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypePhotoLibrary
        picker.mediaTypes = listOf("public.image")
        
        val delegate = ImagePickerDelegate { result ->
            handlePickerResult(result)
        }
        
        pickerDelegate = delegate
        picker.delegate = delegate
        
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(picker, true, null)
    }
    
    private fun presentCameraPicker() {
        val picker = UIImagePickerController()
        picker.sourceType =
            UIImagePickerControllerSourceType.UIImagePickerControllerSourceTypeCamera
        picker.mediaTypes = listOf("public.image")
        picker.cameraCaptureMode =
            UIImagePickerControllerCameraCaptureMode.UIImagePickerControllerCameraCaptureModePhoto
        
        val delegate = ImagePickerDelegate { result ->
            handlePickerResult(result)
        }
        
        pickerDelegate = delegate
        picker.delegate = delegate
        
        val rootViewController = UIApplication.sharedApplication.keyWindow?.rootViewController
        rootViewController?.presentViewController(picker, true, null)
    }
    
    private fun handlePickerResult(imageData: ByteArray?) {
        val continuation = currentContinuation
        currentContinuation = null
        pickerDelegate = null
        
        continuation?.resume(imageData)
    }
    
    @OptIn(ExperimentalForeignApi::class)
    private fun imageToByteArray(image: UIImage): ByteArray? {
        // Usar qualidade extremamente baixa para garantir tamanho mínimo
        val imageData =
            UIImageJPEGRepresentation(image, 0.1) // 10% quality - extremamente comprimido
        return imageData?.let { data ->
            val bytes = ByteArray(data.length.toInt())
            memcpy(bytes.refTo(0), data.bytes, data.length)
            println("DEBUG iOS: Final image size after compression: ${bytes.size} bytes")
            bytes
        }
    }

    // Delegate class para UIImagePickerController
    private class ImagePickerDelegate(
        private val onResult: (ByteArray?) -> Unit
    ) : NSObject(), UIImagePickerControllerDelegateProtocol,
        UINavigationControllerDelegateProtocol {

        override fun imagePickerController(
            picker: UIImagePickerController,
            didFinishPickingMediaWithInfo: Map<Any?, *>
        ) {
            val image =
                didFinishPickingMediaWithInfo[UIImagePickerControllerOriginalImage] as? UIImage
            val imageData = image?.let { convertImageToByteArray(it) }

            // Garantir que o dismiss também seja executado na main thread
            dispatch_async(dispatch_get_main_queue()) {
                picker.dismissViewControllerAnimated(true) {
                    onResult(imageData)
                }
            }
        }

        override fun imagePickerControllerDidCancel(picker: UIImagePickerController) {
            // Garantir que o dismiss também seja executado na main thread
            dispatch_async(dispatch_get_main_queue()) {
                picker.dismissViewControllerAnimated(true) {
                    onResult(null)
                }
            }
        }

        @OptIn(ExperimentalForeignApi::class)
        private fun convertImageToByteArray(image: UIImage): ByteArray? {
            // Tentar diferentes níveis de compressão até ficar abaixo do limite
            var quality = 0.1 // Começar com 10%
            var imageData: NSData?
            var attempts = 0

            do {
                imageData = UIImageJPEGRepresentation(image, quality)
                val sizeBytes = imageData?.length?.toLong() ?: 0L
                println("DEBUG iOS: Compression attempt $attempts with quality $quality - Size: $sizeBytes bytes")

                // Se ainda está muito grande, reduzir mais a qualidade
                if (sizeBytes > 800000L) { // 800KB como limite de segurança
                    quality *= 0.7 // Reduzir em 30%
                    attempts++
                }
            } while ((imageData?.length?.toLong() ?: 0L) > 800000L && attempts < 5)

            return imageData?.let { data ->
                val bytes = ByteArray(data.length.toInt())
                memcpy(bytes.refTo(0), data.bytes, data.length)
                println("DEBUG iOS: Final compressed image size: ${bytes.size} bytes")
                bytes
            }
        }
    }
}

@Composable
actual fun rememberImagePicker(): ImagePicker {
    // Usar o mesmo padrão do Android - assumir que o ImagePicker é fornecido globalmente
    // Por enquanto, criar uma nova instância se não houver CompositionLocal
    return LocalImagePicker.current ?: remember { ImagePicker().apply { initialize() } }
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
                    val imageData = imagePicker.pickImageFromCamera()
                    if (imageData != null) {
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