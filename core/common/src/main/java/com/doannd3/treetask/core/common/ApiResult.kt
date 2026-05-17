package com.doannd3.treetask.core.common

import com.doannd3.treetask.core.common.error.AppErrorCode
import com.doannd3.treetask.core.common.error.toUiText

sealed interface ApiResult<out T> {
    data class Success<out T>(
        val message: UiText? = null,
        val data: T? = null,
    ) : ApiResult<T>

    data class Error(
        // backend message, dùng để display
        val message: UiText? = null,

        // HTTP status: 400, 401, 500
        val statusCode: Int? = null,

        // backend business code
        val backendErrorCode: String? = null,

        // code mobile tự định nghĩa
        val appErrorCode: AppErrorCode? = null,

        val exception: Throwable? = null,
    ) : ApiResult<Nothing>
}

fun ApiResult.Error.toDisplayMessage(fallback: UiText): UiText =
    message ?: appErrorCode?.toUiText() ?: fallback
