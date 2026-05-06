package com.doannd3.treetask.core.domain.usecase.task

import androidx.paging.PagingData
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.datastore.user.UserStorage
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
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
