package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.error.AppErrorCode
import com.doannd3.treetask.core.common.error.MissingResponseDataException
import com.doannd3.treetask.core.data.model.toTaskStats
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
            return when (result) {
                is ApiResult.Success -> {
                    val data = result.data
                    if (data == null) {
                        return ApiResult.Error(
                            appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
                            exception = MissingResponseDataException(),
                        )
                    }

                    ApiResult.Success(
                        message = result.message,
                        data = data.toTaskStats(),
                    )
                }

                is ApiResult.Error -> {
                    result
                }
            }
        }
    }
