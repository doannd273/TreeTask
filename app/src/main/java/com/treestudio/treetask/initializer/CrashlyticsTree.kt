package com.treestudio.treetask.initializer

import android.util.Log
import com.google.firebase.crashlytics.FirebaseCrashlytics
import timber.log.Timber
import javax.inject.Inject

class CrashlyticsTree @Inject constructor(
    private val crashlytics: FirebaseCrashlytics,
) : Timber.Tree() {
    override fun log(
        priority: Int,
        tag: String?,
        message: String,
        t: Throwable?,
    ) {
        // 1. Bỏ qua các log nhảm nhí của hệ thống (VERBOSE, DEBUG)
        if (priority == Log.VERBOSE || priority == Log.DEBUG) {
            return
        }
        // 2. GHI NHÁP (Breadcrumbs): Log.INFO dùng để ghi lại hành trình User
        // Ví dụ: Timber.i("Navigate to TasksScreen") -> Nó chỉ nằm nháp trong RAM (tối đa 64KB)
        val formattedMessage = if (tag != null) "[$tag] $message" else message
        crashlytics.log(formattedMessage)
        // 3. CHỈ BÁO LỖI LÊN SERVER khi thật sự có lỗi (Log.ERROR hoặc Log.WARN)
        if (priority == Log.ERROR || priority == Log.WARN) {
            crashlytics.setCustomKey("priority", priority)

            if (t != null) {
                crashlytics.recordException(t)
            } else {
                // Tự tạo Exception nếu Dev dùng Timber.e("Lỗi gì đó") mà không truyền Throwable
                crashlytics.recordException(Exception(formattedMessage))
            }
        }
    }
}
