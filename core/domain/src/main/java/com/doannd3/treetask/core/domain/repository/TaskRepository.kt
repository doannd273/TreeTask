package com.doannd3.treetask.core.domain.repository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.model.task.Task

interface TaskRepository {
    suspend fun getTasks(page: Int, status: String, keyword: String): ApiResult<Task>

    suspend fun createTask()

    suspend fun editTask()

    suspend fun deleteTask()
}