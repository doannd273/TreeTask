package com.doannd3.treetask.core.network.service

import com.doannd3.treetask.core.network.ApiResponse
import com.doannd3.treetask.core.network.model.request.LoginRequest
import com.doannd3.treetask.core.network.model.request.RefreshTokenRequest
import com.doannd3.treetask.core.network.model.request.RegisterRequest
import com.doannd3.treetask.core.network.model.response.RegisterResponse
import com.doannd3.treetask.core.network.model.response.TokenResponse
import retrofit2.http.Body
import retrofit2.http.POST

interface AuthService {
    @POST("/api/auth/refresh-token")
    suspend fun refreshToken(
        @Body request: RefreshTokenRequest
    ): ApiResponse<TokenResponse>

    @POST("/api/auth/login")
    suspend fun login(
        @Body request: LoginRequest
    ): ApiResponse<TokenResponse>

    @POST("/api/auth/register")
    suspend fun register(
        @Body request: RegisterRequest
    ): ApiResponse<RegisterResponse>
}