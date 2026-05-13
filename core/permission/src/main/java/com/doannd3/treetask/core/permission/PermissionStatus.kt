package com.doannd3.treetask.core.permission

sealed class PermissionStatus {
    data object NotRequired : PermissionStatus()

    data object Granted : PermissionStatus()

    data class Denied(
        val missingPermissions: List<String>,
    ) : PermissionStatus()
}
