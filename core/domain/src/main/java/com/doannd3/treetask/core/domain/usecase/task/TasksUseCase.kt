package com.doannd3.treetask.core.domain.usecase.task

import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.doannd3.treetask.core.domain.repository.TaskRepository
import javax.inject.Inject

class TaskUseCase @Inject constructor(
    private val tasksRepository: TaskRepository
) {
    suspend operator fun invoke(page: Int, status: String, keyword: String) {
        return tasksRepository.getTasks(
            page = page, status = status, keyword = keyword
        )
    }
}