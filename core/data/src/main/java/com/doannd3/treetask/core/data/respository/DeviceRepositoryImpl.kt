package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.DeviceRepository
import com.doannd3.treetask.core.network.model.request.RegisterDeviceTokenRequest
import com.doannd3.treetask.core.network.model.request.UnRegisterTokenRequest
import com.doannd3.treetask.core.network.service.DeviceService
import javax.inject.Inject

class DeviceRepositoryImpl
    @Inject
    constructor(
        private val deviceService: DeviceService,
    ) : DeviceRepository {
        override suspend fun registerToken(token: String): ApiResult<Unit> {
            val body = RegisterDeviceTokenRequest(token = token)
            return deviceService.registerToken(body = body)
        }

        override suspend fun unregisterToken(token: String): ApiResult<Unit> {
            val body = UnRegisterTokenRequest(token = token)
            return deviceService.unregisterToken(body = body)
        }
    }
