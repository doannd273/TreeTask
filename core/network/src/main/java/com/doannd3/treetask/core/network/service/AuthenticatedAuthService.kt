package com.doannd3.treetask.core.network.service

import com.doannd3.treetask.core.common.ApiResult
import retrofit2.http.POST

interface AuthenticatedAuthService {
    @POST("/api/auth/logout")
    suspend fun logout(): ApiResult<Unit>
}
