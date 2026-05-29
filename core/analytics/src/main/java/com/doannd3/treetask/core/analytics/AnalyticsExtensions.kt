package com.doannd3.treetask.core.analytics

/**
 * Tiện ích mở rộng giúp log các event cụ thể một cách có cấu trúc
 */

fun AnalyticsHelper.logLogin(method: String) {
    logEvent(
        AnalyticsEvent(
            type = "login_method",
            extras = listOf(Param("method", method)),
        ),
    )
}

fun AnalyticsHelper.logTaskCreated(
    taskId: String,
    taskType: String,
) {
    logEvent(
        AnalyticsEvent(
            type = "task_created",
            extras =
                listOf(
                    Param("task_id", taskId),
                    Param("task_type", taskType),
                ),
        ),
    )
}

fun AnalyticsHelper.logSyncFailed(reason: String) {
    logEvent(
        AnalyticsEvent(
            type = "sync_failed",
            extras = listOf(Param("reason", reason)),
        ),
    )
}

/**
 * Log các lỗi mạng hoặc lỗi logic quan trọng kèm theo context
 */
fun AnalyticsHelper.logNetworkError(
    url: String,
    errorCode: Int,
    exception: Throwable,
) {
    logError(
        exception = exception,
        message = "Network Error at $url | Code: $errorCode",
    )
}
