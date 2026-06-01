package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.error.AppErrorCode
import com.doannd3.treetask.core.common.error.MissingResponseDataException
import com.doannd3.treetask.core.common.log.AppTag
import com.doannd3.treetask.core.data.model.toUserOrNull
import com.doannd3.treetask.core.datastore.token.TokenStorage
import com.doannd3.treetask.core.datastore.user.UserStorage
import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.doannd3.treetask.core.network.model.request.ForgotPasswordRequest
import com.doannd3.treetask.core.network.model.request.LoginRequest
import com.doannd3.treetask.core.network.model.request.RegisterRequest
import com.doannd3.treetask.core.network.model.request.ResetPasswordRequest
import com.doannd3.treetask.core.network.service.AuthService
import com.doannd3.treetask.core.network.service.AuthenticatedAuthService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import timber.log.Timber
import javax.inject.Inject

class AuthRepositoryImpl
    @Inject
    constructor(
        private val authService: AuthService,
        private val authenticatedAuthService: AuthenticatedAuthService,
        private val tokenStorage: TokenStorage,
        private val userStorage: UserStorage,
    ) : AuthRepository {
        override val isSessionExpired: Flow<Boolean>
            get() = tokenStorage.sessionExpiredEvent.map { true }

        override suspend fun login(
            email: String,
            password: String,
        ): ApiResult<Unit> {
            val result = authService.login(LoginRequest(email = email, password = password))
            return when (result) {
                is ApiResult.Success -> {
                    val data = result.data
                    if (data == null) {
                        return ApiResult.Error(
                            appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
                            exception = MissingResponseDataException(),
                        )
                    }

                    // save profile user
                    val user = data.user.toUserOrNull()
                    if (user == null) {
                        return ApiResult.Error(
                            appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
                            exception = MissingResponseDataException(),
                        )
                    }
                    // save token
                    tokenStorage.saveToken(
                        accessToken = data.accessToken,
                        refreshToken = data.refreshToken,
                    )
                    // save profile
                    userStorage.saveUserProfile(user)

                    ApiResult.Success(data = Unit)
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
        ): ApiResult<String> {
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
                    val data = result.data
                    if (data == null) {
                        return ApiResult.Error(
                            appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
                            exception = MissingResponseDataException(),
                        )
                    }

                    // save profile user
                    val user = data.user.toUserOrNull()
                    if (user == null) {
                        return ApiResult.Error(
                            appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
                            exception = MissingResponseDataException(),
                        )
                    }

                    // save token
                    tokenStorage.saveToken(
                        data.accessToken,
                        data.refreshToken,
                    )

                    // save profile
                    userStorage.saveUserProfile(user)

                    ApiResult.Success(message = result.message)
                }

                is ApiResult.Error -> {
                    result
                } // propagate thẳng
            }
        }

        override suspend fun forgotPassword(email: String): ApiResult<String> {
            val result = authService.forgotPassword(ForgotPasswordRequest(email = email))
            return when (result) {
                is ApiResult.Success -> {
                    ApiResult.Success(message = result.message)
                }

                is ApiResult.Error -> {
                    result
                } // propagate thẳng
            }
        }

        override suspend fun resetPassword(
            email: String,
            otp: String,
            newPassword: String,
        ): ApiResult<String> {
            val body =
                ResetPasswordRequest(
                    email = email,
                    otp = otp,
                    newPassword = newPassword,
                )
            val result = authService.resetPassword(body = body)
            return when (result) {
                is ApiResult.Success -> {
                    ApiResult.Success(message = result.message)
                }

                is ApiResult.Error -> {
                    result
                }
            }
        }

        override suspend fun logout() {
            var logoutResult: ApiResult<Unit>? = null
            try {
                logoutResult = authenticatedAuthService.logout()
            } catch (e: Exception) {
                logoutResult = ApiResult.Error(exception = e)
            } finally {
                logoutResult?.let { result ->
                    if (result is ApiResult.Error) {
                        Timber.tag(AppTag.DATA).e(
                            result.exception,
                            "Logout error: ${result.message}, status code: ${result.statusCode}",
                        )
                    }
                }

                tokenStorage.clearToken()
                userStorage.clearUserProfile()
            }
        }
    }
