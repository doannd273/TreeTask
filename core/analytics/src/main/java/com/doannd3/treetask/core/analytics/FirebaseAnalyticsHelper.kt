package com.doannd3.treetask.core.analytics

import com.google.firebase.analytics.FirebaseAnalytics
import com.google.firebase.analytics.logEvent
import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsHelper
@Inject
constructor(
    private val firebaseCrashlytics: FirebaseCrashlytics,
    private val firebaseAnalytics: FirebaseAnalytics,
) : AnalyticsHelper {
    override fun setUserId(userId: String) {
        firebaseCrashlytics.setUserId(userId)
    }

    override fun clearUserId() {
        firebaseCrashlytics.setUserId("")
    }

    override fun logEvent(event: AnalyticsEvent) {
        firebaseAnalytics.logEvent(event.type) {
            for (param in event.extras) {
                // Firebase quy định value tối đa 100 ký tự để tối ưu băng thông
                param(param.key, param.value.take(MAX_PARAM_LENGTH))
            }
        }
    }

    // Khai báo hằng số ở đây
    companion object {
        private const val MAX_PARAM_LENGTH = 100
    }
}
