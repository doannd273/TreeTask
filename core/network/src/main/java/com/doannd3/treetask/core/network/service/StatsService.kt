package com.doannd3.treetask.core.network.service

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.network.model.response.DashboardResponse
import retrofit2.http.GET

interface StatsService {
    @GET("/api/tasks/getTaskStats")
    suspend fun getTaskStats(): ApiResult<DashboardResponse>
}
