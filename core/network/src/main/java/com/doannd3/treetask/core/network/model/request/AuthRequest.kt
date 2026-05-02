package com.doannd3.treetask.core.network.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class LoginRequest(
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)

@Serializable
data class RefreshTokenRequest(
    @SerialName("refreshToken") val refreshToken: String,
)

@Serializable
data class RegisterRequest(
    @SerialName("fullName") val fullName: String,
    @SerialName("email") val email: String,
    @SerialName("password") val password: String,
)

@Serializable
data class ForgotPasswordRequest(
    @SerialName("email") val email: String,
)
