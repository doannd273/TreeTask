package com.doannd3.treetask.core.network.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class CreateConversationRequest(
    @SerialName("type") val type: String,
    @SerialName("name") val name: String,
    @SerialName("participantIds") val participantIds: List<String> = emptyList(),
)

@Serializable
data class SendMessageRequest(
    @SerialName("conversationId") val conversationId: String,
    @SerialName("content") val content: String,
    @SerialName("type") val type: String,
)
