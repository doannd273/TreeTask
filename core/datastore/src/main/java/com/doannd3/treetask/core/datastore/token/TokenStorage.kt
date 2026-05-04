package com.doannd3.treetask.core.datastore.token

import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.SharedFlow

/**
 * Contract cho việc quản lý token xác thực.
 * Tách biệt interface khỏi implementation để dễ test và mock.
 */
interface TokenStorage {
    /** Luồng báo hiệu phiên đăng nhập hết hạn → UI điều hướng về màn Login */
    val sessionExpiredEvent: SharedFlow<Unit>

    fun getAccessToken(): Flow<String?>

    fun getRefreshToken(): Flow<String?>

    suspend fun saveToken(accessToken: String, refreshToken: String)

    suspend fun clearToken()
}
