package com.doannd3.treetask.core.analytics

import com.google.firebase.crashlytics.FirebaseCrashlytics
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class FirebaseAnalyticsHelper
@Inject
constructor() : AnalyticsHelper {
    override fun setUserId(userId: String) {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setUserId(userId)
    }

    override fun clearUserId() {
        val crashlytics = FirebaseCrashlytics.getInstance()
        crashlytics.setUserId("")
    }
}
