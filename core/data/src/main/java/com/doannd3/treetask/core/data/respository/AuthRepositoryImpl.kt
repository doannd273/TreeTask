package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.datastore.TokenManager
import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.doannd3.treetask.core.network.model.request.ForgotPasswordRequest
import com.doannd3.treetask.core.network.model.request.LoginRequest
import com.doannd3.treetask.core.network.model.request.RegisterRequest
import com.doannd3.treetask.core.network.service.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl @Inject constructor(
    private val authApi: AuthService,
    private val tokenManager: TokenManager,
) : AuthRepository {

    override val isSessionExpired: Flow<Boolean>
        get() = tokenManager.sessionExpiredEvent.map { true }

    override suspend fun login(
        email: String,
        password: String
    ): ApiResult<Unit> {
        return try {
            // Bước 1: Gọi tầng Network lấy DTO
            val networkResponse = authApi.login(LoginRequest(email, password))
            val tokenData = networkResponse.data

            if (networkResponse.success && tokenData != null) {
                tokenManager.saveToken(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken
                )
                ApiResult.Success(Unit) // Cấp báo thành công cho Domain
            } else {
                ApiResult.Error(
                    message = networkResponse.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.common_error_unknown),
                    exception = null
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(
                message = UiText.StringResource(R.string.common_error_network_connection),
                exception = e
            )
        }
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String
    ): ApiResult<Unit> {
        return try {
            val networkResponse = authApi.register(RegisterRequest(fullName, email, password))
            val tokenData = networkResponse.data
            if (networkResponse.success && tokenData != null) {
                tokenManager.saveToken(
                    accessToken = tokenData.accessToken,
                    refreshToken = tokenData.refreshToken
                )
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error(
                    message = networkResponse.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.common_error_unknown),
                    exception = null
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(
                message = UiText.StringResource(R.string.common_error_network_connection),
                exception = e
            )
        }
    }

    override suspend fun forgotPassword(email: String): ApiResult<Unit> {
        return try {
            val networkResponse = authApi.forgotPassword(
                ForgotPasswordRequest(email)
            )
            if (networkResponse.success) {
                ApiResult.Success(Unit)
            } else {
                ApiResult.Error(
                    message = networkResponse.message?.let { UiText.DynamicString(it) }
                        ?: UiText.StringResource(R.string.common_error_unknown),
                    exception = null
                )
            }
        } catch (e: Exception) {
            ApiResult.Error(
                message = UiText.StringResource(R.string.common_error_network_connection),
                exception = e
            )
        }
    }

    override suspend fun logout() {
        tokenManager.clearToken()
    }
}