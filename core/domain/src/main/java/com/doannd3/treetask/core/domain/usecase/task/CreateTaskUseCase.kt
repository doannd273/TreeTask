package com.doannd3.treetask.core.domain.usecase.task

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import javax.inject.Inject

class CreateTaskUseCase
@Inject
constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(
        title: String,
        description: String,
        status: String,
        dueDate: String,
    ): ApiResult<Task> {
        return taskRepository.createTask(
            title = title,
            description = description,
            status = status,
            dueDate = dueDate,
        )
    }
}
