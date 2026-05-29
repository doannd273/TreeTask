package com.doannd3.treetask.core.network.service

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.network.model.request.RegisterDeviceTokenRequest
import retrofit2.http.Body
import retrofit2.http.POST

interface DeviceService {
    @POST("/api/devices/register-token")
    suspend fun registerToken(
        @Body body: RegisterDeviceTokenRequest,
    ): ApiResult<Unit>
}
