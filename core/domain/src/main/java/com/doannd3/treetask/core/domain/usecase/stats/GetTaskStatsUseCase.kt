package com.doannd3.treetask.core.domain.usecase.stats

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.StatsRepository
import com.doannd3.treetask.core.model.stats.TaskStats
import javax.inject.Inject

class GetTaskStatsUseCase @Inject constructor(
    private val statsRepository: StatsRepository
) {
    suspend operator fun invoke(): ApiResult<TaskStats> {
        return statsRepository.getTaskStats()
    }
}