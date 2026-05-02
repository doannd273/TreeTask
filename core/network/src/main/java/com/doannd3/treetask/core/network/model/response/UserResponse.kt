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
    @SerialName("__v") val version: Int? = null,
)

@Serializable
data class AvatarUploadResponse(
    @SerialName("avatar") val avatar: String? = null,
    @SerialName("message") val message: String? = null,
)

@Serializable
data class UsersListResponse(
    @SerialName("users") val users: List<UserResponse> = emptyList(),
    @SerialName("totalItems") val totalItems: Int? = null,
    @SerialName("totalPages") val totalPages: Int? = null,
    @SerialName("currentPage") val currentPage: Int? = null,
)
