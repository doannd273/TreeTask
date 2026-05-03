package com.treestudio.treetask.initializer

import android.app.Application
import com.treestudio.treetask.BuildConfig
import timber.log.Timber
import javax.inject.Inject

class TimberInitializer @Inject constructor(
    private val crashlyticsTree: CrashlyticsTree
) : AppInitializer {

    // Timber phải chạy cực sớm!
    override val priority = InitializationPriority.URGENT

    override fun init(application: Application) {
        if (BuildConfig.DEBUG) {
            // Lúc code ở local: Hiển thị log ở cửa sổ Logcat của Android Studio
            Timber.plant(Timber.DebugTree())
        } else {
            // Lúc đưa lên Play Store: Tắt hết Logcat, chỉ rình lỗi đẩy lên Firebase
            Timber.plant(crashlyticsTree)
        }
    }
}
