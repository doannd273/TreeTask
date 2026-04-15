package com.doannd3.treetask.core.data.model

import com.doannd3.treetask.core.common.extension.toInstantOrNow
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.core.network.model.response.TaskResponse

fun TaskResponse.toTask(): Task {
    return Task(
        id = this.id.orEmpty(),
        userId = this.userId.orEmpty(),
        title = this.title.orEmpty(),
        description = this.description.orEmpty(),
        status = TaskStatus.fromStatus(status),
        dueDate = this.dueDate.toInstantOrNow(),
        createdAt = this.createdAt.toInstantOrNow(),
        updatedAt = this.updatedAt.toInstantOrNow()
    )
}

fun List<TaskResponse>.toTasks(): List<Task> {
    return this.map { it.toTask() }
}