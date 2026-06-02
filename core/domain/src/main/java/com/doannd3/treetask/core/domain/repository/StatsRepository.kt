package com.doannd3.treetask.core.domain.repository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.model.stats.TaskStats

interface StatsRepository {
    suspend fun getTaskStats(): ApiResult<TaskStats>
}
