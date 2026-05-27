package com.doannd3.treetask.core.domain.usecase.task

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.domain.validation.validationError
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
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
        val titleTrimmed = title.trim()
        if (titleTrimmed.isBlank()) {
            return validationError(R.string.common_error_task_title_empty)
        }

        val descriptionTrimmed = description.trim()
        if (descriptionTrimmed.isBlank()) {
            return validationError(R.string.common_error_task_description_empty)
        }

        val statusTrimmed = status.trim()
        if (statusTrimmed.isBlank()) {
            return validationError(R.string.common_error_task_status_empty)
        }
        if (TaskStatus.entries.none { it.apiValue == statusTrimmed }) {
            return validationError(R.string.common_error_task_status_invalid)
        }

        val dueDateTrimmed = dueDate.trim()
        if (dueDateTrimmed.isBlank()) {
            return validationError(R.string.common_error_task_due_date_empty)
        }

        return taskRepository.createTask(
            title = titleTrimmed,
            description = descriptionTrimmed,
            status = statusTrimmed,
            dueDate = dueDateTrimmed,
        )
    }
}
