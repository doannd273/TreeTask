package com.doannd3.treetask.core.domain.repository

import com.doannd3.treetask.core.common.ApiResult
import kotlinx.coroutines.flow.Flow

interface AuthRepository {
    suspend fun login(email: String, password: String): ApiResult<Unit>

    suspend fun register(fullName: String, email: String, password: String): ApiResult<Unit>

    suspend fun forgotPassword(email: String): ApiResult<Unit>

    suspend fun logout()

    val isSessionExpired: Flow<Boolean>
}