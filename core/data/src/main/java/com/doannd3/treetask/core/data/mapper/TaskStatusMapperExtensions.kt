package com.doannd3.treetask.core.data.mapper

import com.doannd3.treetask.core.model.task.TaskStatus

internal fun String?.requiredTaskStatusOrNull(): TaskStatus? {
    return TaskStatus.entries.firstOrNull { taskStatus ->
        taskStatus.apiValue == this
    }
}
