package com.doannd3.treetask.core.domain.usecase.task

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import javax.inject.Inject

class TaskUseCase @Inject constructor(
    private val tasksRepository: TaskRepository
) {
    suspend operator fun invoke(page: Int, status: String, keyword: String): ApiResult<List<Task>> {
        return tasksRepository.getTasks(
            page = page,
            status = status,
            keyword = keyword
        )
    }
}