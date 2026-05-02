package com.doannd3.treetask.core.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskListResponse(
    @SerialName("tasks") val tasks: List<TaskResponse>? = emptyList(),
    @SerialName("totalItems") val totalItems: Int = 0,
    @SerialName("totalPages") val totalPages: Int = 0,
    @SerialName("currentPage") val currentPage: Int = 0,
)

@Serializable
data class TaskResponse(
    @SerialName("_id") val id: String? = null,
    @SerialName("userId") val userId: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("description") val description: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("dueDate") val dueDate: String? = null,
    @SerialName("__v") val version: Int? = null,
)
