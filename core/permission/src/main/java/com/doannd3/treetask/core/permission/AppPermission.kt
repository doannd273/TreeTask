package com.doannd3.treetask.core.permission

import android.Manifest
import android.os.Build

sealed class AppPermission(
    val manifestNames: List<String>,
    val requestFromSdk: Int = Build.VERSION_CODES.M,
) {
    data object PostNotification : AppPermission(
        manifestNames = listOf(Manifest.permission.POST_NOTIFICATIONS),
        requestFromSdk = Build.VERSION_CODES.TIRAMISU,
    )

    data object Camera : AppPermission(
        manifestNames = listOf(Manifest.permission.CAMERA),
        requestFromSdk = Build.VERSION_CODES.M,
    )
}
