package com.doannd3.treetask.core.domain.usecase.device

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.SessionRepository
import javax.inject.Inject

class RegisterDeviceTokenIfAuthenticatedUseCase
    @Inject
    constructor(
        private val sessionRepository: SessionRepository,
        private val registerDeviceTokenUseCase: RegisterDeviceTokenUseCase,
    ) {
        suspend operator fun invoke(token: String): ApiResult<Unit>? {
            if (!sessionRepository.hasStoredSession()) {
                return null
            }

            return registerDeviceTokenUseCase(token = token)
        }
    }
