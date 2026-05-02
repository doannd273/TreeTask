package com.doannd3.treetask.core.network.interceptor

import android.content.Context
import android.os.Build
import com.doannd3.treetask.core.datastore.DevicePrefsManager
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Response
import java.util.Locale
import javax.inject.Inject

class CommonHeaderInterceptor
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
        private val devicePrefsManager: DevicePrefsManager,
    ) : Interceptor {
        override fun intercept(chain: Interceptor.Chain): Response {
            val packageInfo = context.packageManager.getPackageInfo(context.packageName, 0)
            val versionCode =
                if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
                    packageInfo.longVersionCode.toString()
                } else {
                    @Suppress("DEPRECATION")
                    packageInfo.versionCode.toString()
                }

            val versionName = packageInfo.versionName
            val deviceId = runBlocking { devicePrefsManager.getDeviceId() }

            val originalRequest = chain.request()
            val requestWithHeaders =
                originalRequest.newBuilder()
                    .header("X-App-Version-Code", versionCode)
                    .header("X-App-Version-Name", versionName ?: "unknown")
                    .header("X-Platform", "android")
                    .header("X-OS-Version", Build.VERSION.SDK_INT.toString())
                    .header("X-Device-Model", Build.MODEL)
                    .header("X-Device-Id", deviceId)
                    .header(
                        "User-Agent",
                        "TreeTask/$versionName (Android ${Build.VERSION.RELEASE}; ${Build.MODEL})",
                    )
                    .header("Accept-Language", Locale.getDefault().language)
                    .build()
            return chain.proceed(requestWithHeaders)
        }
    }
