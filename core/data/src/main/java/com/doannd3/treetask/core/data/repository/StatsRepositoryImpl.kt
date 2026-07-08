package com.doannd3.treetask.core.data.repository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.data.mapper.toTaskStatsOrNull
import com.doannd3.treetask.core.domain.repository.StatsRepository
import com.doannd3.treetask.core.model.stats.TaskStats
import com.doannd3.treetask.core.network.service.StatsService
import javax.inject.Inject

class StatsRepositoryImpl
@Inject
constructor(
    private val statsService: StatsService,
) : StatsRepository {
    override suspend fun getTaskStats(): ApiResult<TaskStats> {
        val result = statsService.getTaskStats()
        return result.mapSuccessResult { success ->
            val data = success.data ?: return@mapSuccessResult missingResponseDataError()
            val taskStats =
                data.toTaskStatsOrNull()
                    ?: return@mapSuccessResult missingResponseDataError()

            ApiResult.Success(
                message = success.message,
                data = taskStats,
            )
        }
    }
}
