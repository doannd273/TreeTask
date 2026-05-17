package com.doannd3.treetask.core.domain.repository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.model.user.ChangePassword
import com.doannd3.treetask.core.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {
    suspend fun getProfile(): ApiResult<User>

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
    ): ApiResult<ChangePassword>

    fun getCachedProfile(): Flow<User?>
}
