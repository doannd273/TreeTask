package com.doannd3.treetask.core.network.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class TaskRequest(
    @SerialName("title") val title: String,
    @SerialName("description") val description: String,
    @SerialName("status") val status: String,
    @SerialName("dueDate") val dueDate: String,
)