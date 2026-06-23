package com.doannd3.treetask.core.domain.usecase.device

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.DeviceRepository
import com.doannd3.treetask.core.domain.validation.validationError
import javax.inject.Inject

class RegisterDeviceTokenUseCase
    @Inject
    constructor(
        private val deviceRepository: DeviceRepository,
    ) {
        suspend operator fun invoke(token: String): ApiResult<Unit> {
            val tokenTrimmed = token.trim()
            if (tokenTrimmed.isBlank()) {
                return validationError(R.string.common_error_device_token_empty)
            }

            return deviceRepository.registerToken(token = tokenTrimmed)
        }
    }
