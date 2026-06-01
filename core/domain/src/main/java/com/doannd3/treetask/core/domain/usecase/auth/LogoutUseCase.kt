package com.doannd3.treetask.core.domain.usecase.auth

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.doannd3.treetask.core.domain.repository.PushTokenProvider
import com.doannd3.treetask.core.domain.usecase.device.UnRegisterDeviceTokenUseCase
import javax.inject.Inject

data class LogoutResult(
    val unregisterDeviceTokenResult: ApiResult<Unit>? = null,
)

class LogoutUseCase
    @Inject
    constructor(
        private val unRegisterDeviceTokenUseCase: UnRegisterDeviceTokenUseCase,
        private val authRepository: AuthRepository,
        private val pushTokenProvider: PushTokenProvider,
    ) {
        suspend operator fun invoke(): LogoutResult {
            var unregisterResult: ApiResult<Unit>? = null
            try {
                // clear token firebase
                val token = pushTokenProvider.getToken()
                if (token != null) {
                    unregisterResult = unRegisterDeviceTokenUseCase(token = token)
                }
            } catch (e: Exception) {
                unregisterResult = ApiResult.Error(exception = e)
            } finally {
                authRepository.logout()
            }

            return LogoutResult(unregisterDeviceTokenResult = unregisterResult)
        }
    }
