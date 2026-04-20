package com.doannd3.treetask.core.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class UserResponse(
    @SerialName("_id") val id: String? = null,
    @SerialName("fullName") val fullName: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("avatar") val avatar: String? = null,
    @SerialName("phone") val phone: String? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("__v") val version: Int? = null
)