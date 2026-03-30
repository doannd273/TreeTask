package com.doannd3.treetask.core.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TokenResponse(
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String,
)

// Cho riêng API Register
@Serializable
data class RegisterResponse(
    @SerialName("userId") val userId: String,
    @SerialName("accessToken") val accessToken: String,
    @SerialName("refreshToken") val refreshToken: String
    // Sau này Backend có thể trả thêm "email", "avatar", "createdAt" ở chỗ này!
)