package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.error.AppErrorCode
import com.doannd3.treetask.core.common.error.MissingResponseDataException

internal fun missingResponseDataError(): ApiResult.Error {
    return ApiResult.Error(
        appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
        exception = MissingResponseDataException(),
    )
}
