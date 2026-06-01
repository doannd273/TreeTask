package com.doannd3.treetask.core.domain.repository

import com.doannd3.treetask.core.common.ApiResult

interface DeviceRepository {
    suspend fun registerToken(token: String): ApiResult<Unit>

    suspend fun unregisterToken(token: String): ApiResult<Unit>
}
