package com.doannd3.treetask.core.common

sealed interface ApiResult<out T> {
    data class Success<out T>(
        val data: T,
    ) : ApiResult<T>

    data class Error(
        val message: UiText? = null,  // backend message, dùng để display
        val statusCode: Int? = null, // HTTP status: 400, 401, 500
        val errorCode: String? = null,    // backend business code
        val exception: Throwable?,
    ) : ApiResult<Nothing>
}
