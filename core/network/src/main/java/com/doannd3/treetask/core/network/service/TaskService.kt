package com.doannd3.treetask.core.network.service

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.network.model.response.TaskListResponse
import com.doannd3.treetask.core.network.model.response.TaskResponse
import retrofit2.http.GET
import retrofit2.http.Query

interface TaskService {

    @GET("/api/tasks/getTasks")
    suspend fun getTasks(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: String,
        @Query("keyword") keyword: String
    ): ApiResult<TaskListResponse>
}