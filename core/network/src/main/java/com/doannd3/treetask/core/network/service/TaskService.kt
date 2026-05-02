package com.doannd3.treetask.core.network.service

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.network.model.request.TaskRequest
import com.doannd3.treetask.core.network.model.response.TaskListResponse
import com.doannd3.treetask.core.network.model.response.TaskResponse
import retrofit2.http.Body
import retrofit2.http.DELETE
import retrofit2.http.GET
import retrofit2.http.POST
import retrofit2.http.PUT
import retrofit2.http.Path
import retrofit2.http.Query

interface TaskService {
    @GET("/api/tasks/getTasks")
    suspend fun getTasks(
        @Query("page") page: Int,
        @Query("limit") limit: Int,
        @Query("status") status: String,
        @Query("keyword") keyword: String,
    ): ApiResult<TaskListResponse>

    @DELETE("/api/tasks/deleteTask/{taskId}")
    suspend fun deleteTask(
        @Path("taskId") taskId: String,
    ): ApiResult<Unit>

    @POST("/api/tasks/createTask")
    suspend fun createTask(
        @Body request: TaskRequest,
    ): ApiResult<TaskResponse>

    @PUT("/api/tasks/updateTask/{taskId}")
    suspend fun updateTask(
        @Path("taskId") taskId: String,
        @Body request: TaskRequest,
    ): ApiResult<TaskResponse>
}
