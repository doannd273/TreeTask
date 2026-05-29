package com.doannd3.treetask.core.domain.usecase.device

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.DeviceRepository
import javax.inject.Inject

class RegisterDeviceTokenUseCase
    @Inject
    constructor(
        private val deviceRepository: DeviceRepository,
    ) {
        suspend operator fun invoke(token: String): ApiResult<Unit> {
            return deviceRepository.registerToken(token = token)
        }
    }
