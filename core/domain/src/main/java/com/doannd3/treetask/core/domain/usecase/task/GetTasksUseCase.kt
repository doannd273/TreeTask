package com.doannd3.treetask.core.domain.usecase.task

import androidx.paging.PagingData
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val tasksRepository: TaskRepository,
) {
    operator fun invoke(
        status: String,
        keyword: String,
        userId: String,
    ): Flow<PagingData<Task>> {
        val userIdTrimmed = userId.trim()
        if (userIdTrimmed.isBlank()) {
            return flowOf(PagingData.empty<Task>())
        }

        val statusTrimmed = status.trim()
        val normalizedStatus =
            statusTrimmed.takeIf { value ->
                value.isBlank() ||
                    TaskStatus.entries.any { taskStatus ->
                        taskStatus.apiValue == value
                    }
            } ?: return flowOf(PagingData.empty<Task>())

        return tasksRepository.getTasks(
            status = normalizedStatus,
            keyword = keyword.trim(),
            userId = userIdTrimmed,
        )
    }
}
