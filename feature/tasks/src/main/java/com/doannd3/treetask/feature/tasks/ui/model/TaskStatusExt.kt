package com.doannd3.treetask.feature.tasks.ui.model

import androidx.annotation.StringRes
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.feature.tasks.R

@StringRes
fun TaskStatus.labelRes(): Int =
    when (this) {
        TaskStatus.TODO -> R.string.tasks_status_todo
        TaskStatus.IN_PROGRESS -> R.string.tasks_status_in_progress
        TaskStatus.PENDING -> R.string.tasks_status_pending
        TaskStatus.DONE -> R.string.tasks_status_done
    }
