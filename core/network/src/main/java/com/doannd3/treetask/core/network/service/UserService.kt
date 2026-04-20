package com.doannd3.treetask.core.network.service

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.network.model.response.UserResponse
import retrofit2.http.GET

interface UserService {

    @GET("/api/user/getProfile")
    suspend fun getProfile(): ApiResult<UserResponse>
}