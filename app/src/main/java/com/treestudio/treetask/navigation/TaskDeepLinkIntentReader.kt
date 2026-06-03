package com.treestudio.treetask.navigation

import android.content.Intent
import com.doannd3.treetask.core.notification.NotificationHelper

private const val TASK_ID_DATA_EXTRA = "taskId"

internal fun Intent?.readTaskIdExtra(): String? {
    val taskId =
        this?.getStringExtra(NotificationHelper.EXTRA_TASK_ID)
            ?: this?.getStringExtra(TASK_ID_DATA_EXTRA)

    return taskId?.trim()?.takeIf { it.isNotEmpty() }
}

internal fun Intent.clearTaskIdExtras() {
    removeExtra(NotificationHelper.EXTRA_TASK_ID)
    removeExtra(TASK_ID_DATA_EXTRA)
}
