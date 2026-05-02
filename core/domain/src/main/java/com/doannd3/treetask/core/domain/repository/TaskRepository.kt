package com.doannd3.treetask.core.domain.repository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.model.task.Task
import kotlinx.coroutines.flow.Flow

interface TaskRepository {
    /**
     * Luồng dữ liệu để UI quan sát
     */
    fun getTasksStream(userId: String): Flow<List<Task>>

    /**
     * Lệnh đồng b dữ liệu từ Server về (Action) - Trả về Unit vì data sẽ chảy về qua Flow trên
     */
    suspend fun syncTasks(userId: String): ApiResult<Unit>
}
