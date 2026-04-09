package com.doannd3.treetask.core.network.util

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.network.ApiResponse
import kotlinx.serialization.json.Json
import retrofit2.Call
import retrofit2.CallAdapter
import retrofit2.Retrofit
import java.lang.reflect.ParameterizedType
import java.lang.reflect.Type

class ApiResultCallAdapterFactory(private val json: Json) : CallAdapter.Factory() {
    override fun get(returnType: Type, annotations: Array<Annotation>, retrofit: Retrofit): CallAdapter<*, *>? {
        // Với suspend fun, returnType luôn được bọc trong Call<T>
        if (getRawType(returnType) != Call::class.java) return null
        check(returnType is ParameterizedType) { "Call phải có type parameter" }

        val callType = getParameterUpperBound(0, returnType)
        // Chỉ xử lý nếu bên trong Call là ApiResult<T>
        if (getRawType(callType) != ApiResult::class.java) return null
        check(callType is ParameterizedType) { "ApiResult phải có type parameter" }

        // Lấy T từ ApiResult<T>  →  ví dụ: TokenResponse
        val innerType: Type = getParameterUpperBound(0, callType)

        // Tạo ApiResponse<T> để Retrofit biết deserialize gì
        val responseType = object : ParameterizedType {
            override fun getActualTypeArguments(): Array<Type> = arrayOf(innerType)
            override fun getRawType(): Type = ApiResponse::class.java
            override fun getOwnerType(): Type? = null
        }

        return ApiResultCallAdapter<Any>(responseType, json)
    }
}