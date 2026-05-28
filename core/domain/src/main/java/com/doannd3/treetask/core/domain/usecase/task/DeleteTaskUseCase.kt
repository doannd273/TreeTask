package com.doannd3.treetask.core.domain.usecase.task

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.TaskRepository
import javax.inject.Inject

class DeleteTaskUseCase @Inject constructor(
    private val taskRepository: TaskRepository,
) {
    suspend operator fun invoke(taskId: String): ApiResult<Unit> =
        taskRepository.deleteTask(taskId = taskId)
}
