package com.doannd3.treetask.core.permission

import android.content.Context
import android.content.pm.PackageManager
import android.os.Build

class PermissionChecker(
    private val context: Context,
) {
    fun check(permission: AppPermission): PermissionStatus {
        if (Build.VERSION.SDK_INT < permission.requestFromSdk) {
            return PermissionStatus.NotRequired
        }

        val missingPermissions =
            permission.manifestNames.filter { manifestName ->
                context.checkSelfPermission(manifestName) != PackageManager.PERMISSION_GRANTED
            }
        return if (missingPermissions.isEmpty()) {
            PermissionStatus.Granted
        } else {
            PermissionStatus.Denied(missingPermissions = missingPermissions)
        }
    }

    fun canUse(permission: AppPermission): Boolean {
        val permissionStatus = check(permission = permission)
        return permissionStatus == PermissionStatus.Granted ||
            permissionStatus == PermissionStatus.NotRequired
    }
}
