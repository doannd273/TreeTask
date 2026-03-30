package com.doannd3.treetask.core.network

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ApiResponse<T>(
    @SerialName("success") val success: Boolean,
    @SerialName("message") val message: String?,
    @SerialName("data") val data: T?
)