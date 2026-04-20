package com.doannd3.treetask.core.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class DashboardResponse(
    @SerialName("total") val total: Int? = null,
    @SerialName("todo") val todo: Int? = null,
    @SerialName("inProgress") val inProgress: Int? = null,
    @SerialName("pending") val pending: Int? = null,
    @SerialName("done") val done: Int? = null,
    @SerialName("completionRate") val completionRate: Int? = null,
    @SerialName("recentTasks") val recentTasks: List<RecentTask> = emptyList()
)

@Serializable
data class RecentTask(
    @SerialName("_id") val id: String? = null,
    @SerialName("title") val title: String? = null,
    @SerialName("status") val status: String? = null,
    @SerialName("createdAt") val createdAt: String? = null
)