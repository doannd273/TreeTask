package com.doannd3.treetask.core.data.repository

import com.doannd3.treetask.core.common.ApiResult

internal inline fun <T, R> ApiResult<T>.mapSuccessResult(transform: (ApiResult.Success<T>) -> ApiResult<R>): ApiResult<R> {
    return when (this) {
        is ApiResult.Success -> transform(this)
        is ApiResult.Error -> this
    }
}
