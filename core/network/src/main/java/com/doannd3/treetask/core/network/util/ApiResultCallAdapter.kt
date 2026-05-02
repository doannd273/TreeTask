package com.doannd3.treetask.core.network.util

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.network.ApiResponse
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.CallAdapter
import java.lang.reflect.Type

class ApiResultCallAdapter<T>(
    private val responseType: Type,
    private val json: Json,
) : CallAdapter<ApiResponse<T>, Call<ApiResult<T>>> {
    // Nói với Retrofit: deserialize HTTP body thành ApiResponse<T>
    override fun responseType(): Type = responseType

    // Bọc Call<ApiResponse<T>> → Call<ApiResult<T>>
    override fun adapt(call: Call<ApiResponse<T>>): Call<ApiResult<T>> {
        return ApiResultCall(call, json)
    }
}
