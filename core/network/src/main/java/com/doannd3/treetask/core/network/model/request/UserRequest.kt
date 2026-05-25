package com.doannd3.treetask.core.network.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordRequest(
    @SerialName("currentPassword") val currentPassword: String,
    @SerialName("newPassword") val newPassword: String,
)

@Serializable
data class ResetPasswordRequest(
    @SerialName("email") val email: String,
    @SerialName("otp") val otp: String,
    @SerialName("newPassword") val newPassword: String,
)

@Serializable
data class UpdateProfileRequest(
    @SerialName("fullName") val fullName: String,
    @SerialName("phone") val phone: String,
    @SerialName("avatar") val avatar: String,
)
