package com.doannd3.treetask.core.domain.repository

import android.net.Uri
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.model.user.User
import kotlinx.coroutines.flow.Flow

interface UserRepository {

    suspend fun uploadFile(
        uri: Uri,
    ): ApiResult<String>

    suspend fun updateProfile(
        fullName: String,
        phone: String,
        avatar: String,
    ): ApiResult<User>

    suspend fun getProfile(): ApiResult<User>

    suspend fun changePassword(
        currentPassword: String,
        newPassword: String,
    ): ApiResult<String>

    fun getCachedProfile(): Flow<User?>
}
