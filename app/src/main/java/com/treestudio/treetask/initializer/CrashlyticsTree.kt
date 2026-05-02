package com.treestudio.treetask.initializer

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber

class CrashlyticsTree : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        // KHÔNG LOG những thông tin thừa thãi (như debug API, click button...) lên Server
        if (priority == Log.VERBOSE || priority == Log.DEBUG || priority == Log.INFO) {
            return
        }
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setCustomKey("priority", priority)
        tag?.let { crashlytics.setCustomKey("tag", it) }
        crashlytics.log(message)
        if (t != null) {
            crashlytics.recordException(t)
        } else {
            // Đẩy lên dưới dạng Non-Fatal Exception nếu có dòng Log.e() nhưng không kèm Throwable
            crashlytics.recordException(Exception(message))
        }
    }
}
