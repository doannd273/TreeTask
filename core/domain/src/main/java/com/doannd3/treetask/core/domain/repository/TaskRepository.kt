package com.doannd3.treetask.core.domain.repository

import androidx.paging.PagingData
import com.doannd3.treetask.core.model.task.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    fun getTasks(
        status: String,
        keyword: String,
        userId: String,
    ): Flow<PagingData<Task>>

    suspend fun syncTasks(userId: String): com.doannd3.treetask.core.common.ApiResult<Unit>
}
