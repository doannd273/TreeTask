package com.treestudio.treetask.initializer

import android.app.Application
import android.os.Build
import com.doannd3.treetask.core.common.log.AppTag
import com.treestudio.treetask.BuildConfig
import timber.log.Timber
import javax.inject.Inject

class AppInfoInitializer @Inject constructor() : AppInitializer {

    override val priority = InitializationPriority.NORMAL

    override fun init(application: Application) {
        if (!BuildConfig.DEBUG) return

        val isEmulator = Build.FINGERPRINT.contains("generic") ||
            Build.MODEL.contains("Emulator") ||
            Build.MODEL.contains("Android SDK")

        Timber.tag(AppTag.APP_INFO).d(
            """
            ┌─────────────────────────────────────────┐
            │              APP BUILD INFO              │
            ├─────────────────────────────────────────┤
            │ App ID     : ${BuildConfig.APPLICATION_ID}
            │ Version    : ${BuildConfig.VERSION_NAME} (build ${BuildConfig.VERSION_CODE})
            │ Build Type : ${BuildConfig.BUILD_TYPE}
            │ Flavor     : ${BuildConfig.FLAVOR}
            ├─────────────────────────────────────────┤
            │ Device     : ${Build.MANUFACTURER} ${Build.MODEL}
            │ Android    : ${Build.VERSION.RELEASE} (API ${Build.VERSION.SDK_INT})
            │ Emulator   : $isEmulator
            └─────────────────────────────────────────┘
            """.trimIndent(),
        )
    }

}
