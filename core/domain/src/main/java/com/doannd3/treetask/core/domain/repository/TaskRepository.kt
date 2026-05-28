package com.doannd3.treetask.core.domain.repository

import androidx.paging.PagingData
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.model.task.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(
        status: String,
        keyword: String,
        userId: String,
    ): Flow<PagingData<Task>>

    suspend fun syncTasks(userId: String): ApiResult<Unit>

    suspend fun createTask(
        title: String,
        description: String,
        status: String,
        dueDate: String,
    ): ApiResult<Task>

    suspend fun updateTask(
        taskId: String,
        title: String,
        description: String,
        status: String,
        dueDate: String,
    ): ApiResult<Task>

    suspend fun getTaskById(taskId: String): ApiResult<Task>
}
