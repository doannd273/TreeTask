package com.doannd3.treetask.core.domain.usecase.device

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.PushTokenProvider
import javax.inject.Inject

class RegisterCurrentDeviceTokenUseCase
    @Inject
    constructor(
        private val pushTokenProvider: PushTokenProvider,
        private val registerDeviceTokenUseCase: RegisterDeviceTokenUseCase,
    ) {
        suspend operator fun invoke(): ApiResult<Unit>? {
            val token = pushTokenProvider.getToken() ?: return null
            return registerDeviceTokenUseCase(token)
        }
    }
