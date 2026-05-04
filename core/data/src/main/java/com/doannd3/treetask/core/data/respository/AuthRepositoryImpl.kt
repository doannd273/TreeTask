package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.datastore.TokenManager
import com.doannd3.treetask.core.datastore.UserPrefsManager
import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.doannd3.treetask.core.network.model.request.ForgotPasswordRequest
import com.doannd3.treetask.core.network.model.request.LoginRequest
import com.doannd3.treetask.core.network.model.request.RegisterRequest
import com.doannd3.treetask.core.network.service.AuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class AuthRepositoryImpl
@Inject
constructor(
    private val authService: AuthService,
    private val tokenManager: TokenManager,
    private val userPrefsManager: UserPrefsManager,
) : AuthRepository {
    override val isSessionExpired: Flow<Boolean>
        get() = tokenManager.sessionExpiredEvent.map { true }

    override suspend fun login(
        email: String,
        password: String,
    ): ApiResult<Unit> {
        val result = authService.login(LoginRequest(email = email, password = password))
        return when (result) {
            is ApiResult.Success -> {
                tokenManager.saveToken(
                    accessToken = result.data.accessToken,
                    refreshToken = result.data.refreshToken,
                )
                ApiResult.Success(Unit)
            }

            is ApiResult.Error -> {
                result
            } // propagate thẳng
        }
    }

    override suspend fun register(
        fullName: String,
        email: String,
        password: String,
    ): ApiResult<Unit> {
        val result =
            authService.register(
                RegisterRequest(
                    fullName = fullName,
                    email = email,
                    password = password,
                ),
            )
        return when (result) {
            is ApiResult.Success -> {
                tokenManager.saveToken(result.data.accessToken, result.data.refreshToken)
                ApiResult.Success(Unit)
            }

            is ApiResult.Error -> {
                result
            } // propagate thẳng
        }
    }

    override suspend fun forgotPassword(email: String): ApiResult<Unit> {
        val result = authService.forgotPassword(ForgotPasswordRequest(email = email))
        return when (result) {
            is ApiResult.Success -> {
                ApiResult.Success(Unit)
            }

            is ApiResult.Error -> {
                result
            } // propagate thẳng
        }
    }

    override suspend fun logout() {
        tokenManager.clearToken()
        userPrefsManager.clearUserProfile()
    }
}
