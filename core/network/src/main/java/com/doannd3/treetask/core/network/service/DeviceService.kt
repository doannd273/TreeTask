package com.doannd3.treetask.core.network.service

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.network.model.request.RegisterDeviceTokenRequest
import com.doannd3.treetask.core.network.model.request.UnRegisterTokenRequest
import retrofit2.http.Body
import retrofit2.http.HTTP
import retrofit2.http.POST

interface DeviceService {
    @POST("/api/devices/register-token")
    suspend fun registerToken(
        @Body body: RegisterDeviceTokenRequest,
    ): ApiResult<Unit>

    @HTTP(method = "DELETE", path = "/api/devices/unregister-token", hasBody = true)
    suspend fun unregisterToken(
        @Body body: UnRegisterTokenRequest,
    ): ApiResult<Unit>
}
