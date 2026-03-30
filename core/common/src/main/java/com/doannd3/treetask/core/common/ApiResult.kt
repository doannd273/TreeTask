package com.doannd3.treetask.core.common

sealed interface ApiResult<out T> {
    data class Success<out T>(val data: T) : ApiResult<T>

    data object Loading : ApiResult<Nothing>

    data class Error(val message: UiText? = null, val exception: Throwable?) : ApiResult<Nothing>
}