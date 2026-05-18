package com.doannd3.treetask.core.domain.repository

import com.doannd3.treetask.core.common.ApiResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {

    val isSessionExpired: Flow<Boolean>

    suspend fun login(
        email: String,
        password: String,
    ): ApiResult<Unit>

    suspend fun register(
        fullName: String,
        email: String,
        password: String,
    ): ApiResult<String>

    suspend fun forgotPassword(email: String): ApiResult<String>

    suspend fun resetPassword(
        email: String,
        otp: String,
        newPassword: String,
    ): ApiResult<String>

    suspend fun logout()
}
