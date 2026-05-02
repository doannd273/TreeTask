package com.doannd3.treetask.core.network.util

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import okhttp3.Request
import okio.Timeout
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException

class ApiResultCall<T>(
    private val delegate: Call<ApiResponse<T>>,
    private val json: Json,
) : Call<ApiResult<T>> {
    override fun enqueue(callback: Callback<ApiResult<T>>) {
        delegate.enqueue(
            object : Callback<ApiResponse<T>> {
                override fun onResponse(
                    call: Call<ApiResponse<T>>,
                    response: Response<ApiResponse<T>>,
                ) {
                    val result: ApiResult<T> =
                        if (response.isSuccessful) {
                            val body = response.body()
                            if (body != null && body.success == true) {
                                @Suppress("UNCHECKED_CAST")
                                ApiResult.Success((body.data ?: Unit) as T)
                            } else {
                                ApiResult.Error(
                                    message =
                                        body?.message?.let { UiText.DynamicString(it) }
                                            ?: UiText.StringResource(R.string.common_error_unknown),
                                    exception = null,
                                )
                            }
                        } else {
                            // 4xx / 5xx → parse errorBody lấy message từ BE
                            val errorMessage =
                                try {
                                    val errorJson = response.errorBody()?.string()
                                    json.decodeFromString<ApiResponse<Unit>>(
                                        errorJson ?: "",
                                    ).message
                                } catch (e: SerializationException) {
                                    e.printStackTrace()
                                    null
                                } catch (e: IOException) {
                                    e.printStackTrace()
                                    null
                                }
                            ApiResult.Error(
                                message =
                                    errorMessage?.let { UiText.DynamicString(it) }
                                        ?: UiText.StringResource(R.string.common_error_unknown),
                                errorCode = response.code(),
                                exception = null,
                            )
                        }
                    // Luôn onResponse (không throw) để coroutine nhận về ApiResult
                    callback.onResponse(this@ApiResultCall, Response.success(result))
                }

                override fun onFailure(
                    call: Call<ApiResponse<T>>,
                    t: Throwable,
                ) {
                    val error =
                        when (t) {
                            is IOException ->
                                ApiResult.Error(
                                    message = UiText.StringResource(R.string.common_error_network_connection),
                                    exception = t,
                                )

                            else ->
                                ApiResult.Error(
                                    message = UiText.StringResource(R.string.common_error_unknown),
                                    exception = t,
                                )
                        }
                    callback.onResponse(this@ApiResultCall, Response.success(error))
                }
            },
        )
    }

    // Delegate các method còn lại
    override fun clone(): Call<ApiResult<T>> = ApiResultCall(delegate.clone(), json)

    override fun execute(): Response<ApiResult<T>> = throw UnsupportedOperationException("Chỉ dùng với suspend")

    override fun isExecuted(): Boolean = delegate.isExecuted

    override fun cancel() = delegate.cancel()

    override fun isCanceled(): Boolean = delegate.isCanceled

    override fun request(): Request = delegate.request()

    override fun timeout(): Timeout = delegate.timeout()
}
