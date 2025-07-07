package com.miga.piggy.utils.permission

import androidx.compose.material3.SnackbarDuration
import androidx.compose.material3.SnackbarHostState
import androidx.compose.material3.SnackbarResult
import dev.icerock.moko.permissions.DeniedAlwaysException
import dev.icerock.moko.permissions.DeniedException
import dev.icerock.moko.permissions.Permission
import dev.icerock.moko.permissions.PermissionsController
import dev.icerock.moko.permissions.RequestCanceledException

expect fun isDesktopPlatform(): Boolean

suspend fun checkPermission(
    permission: Permission,
    controller: PermissionsController,
    snackBarHostState: SnackbarHostState
): Boolean {

    if (isDesktopPlatform()) {
        return true
    }

    val granted = controller.isPermissionGranted(permission)

    if (!granted) {
        try {
            controller.providePermission(permission)
            return true
        } catch (e: DeniedException) {
            val result = snackBarHostState.showSnackbar(
                message = e.message ?: "Permission denied",
                actionLabel = "Open Settings",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                controller.openAppSettings()
            }
            return false
        } catch (e: DeniedAlwaysException) {
            val result = snackBarHostState.showSnackbar(
                message = e.message ?: "Permission denied permanently",
                actionLabel = "Open Settings",
                duration = SnackbarDuration.Short
            )
            if (result == SnackbarResult.ActionPerformed) {
                controller.openAppSettings()
            }
            return false
        } catch (e: RequestCanceledException) {
            snackBarHostState.showSnackbar(e.message ?: "Permission request canceled")
            return false
        }
    } else {
        snackBarHostState.showSnackbar("Permission already granted")
        return false
    }
}