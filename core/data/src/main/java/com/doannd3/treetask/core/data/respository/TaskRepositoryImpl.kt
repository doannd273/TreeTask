package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.network.model.response.TaskListResponse
import com.doannd3.treetask.core.network.service.TaskService
import javax.inject.Inject

class TaskRepositoryImpl @Inject constructor(
    private val taskService: TaskService
): TaskRepository {


    override suspend fun getTasks(
        page: Int,
        status: String,
        keyword: String
    ): ApiResult<Task> {
        val result = taskService.getTasks(
            page = page,
            limit = LIMIT,
            status = status,
            keyword = keyword
        )
        return when(result) {
            is ApiResult.Success -> {

            }
            is ApiResult.Error -> result
        }
    }

    override suspend fun createTask() {
        
    }

    override suspend fun editTask() {
        
    }

    override suspend fun deleteTask() {
        
    }

    companion object {
        const val LIMIT = 10
    }
}
