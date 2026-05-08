package com.doannd3.treetask.core.domain.usecase.task

import androidx.paging.PagingData
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class GetTasksUseCase
@Inject
constructor(
    private val tasksRepository: TaskRepository,
) {
    operator fun invoke(
        status: String,
        keyword: String,
        userId: String,
    ): Flow<PagingData<Task>> {
        return tasksRepository.getTasks(status = status, keyword = keyword, userId = userId)
    }
}
