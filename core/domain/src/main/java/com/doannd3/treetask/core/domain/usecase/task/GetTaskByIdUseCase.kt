package com.doannd3.treetask.core.domain.usecase.task

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.domain.validation.validationError
import com.doannd3.treetask.core.model.task.Task
import javax.inject.Inject

class GetTaskByIdUseCase
    @Inject
    constructor(
        private val taskRepository: TaskRepository,
    ) {
        suspend operator fun invoke(taskId: String): ApiResult<Task> {
            val taskIdTrimmed = taskId.trim()
            if (taskIdTrimmed.isBlank()) {
                return validationError(R.string.common_error_task_id_empty)
            }

            return taskRepository.getTaskById(taskId = taskIdTrimmed)
        }
    }
