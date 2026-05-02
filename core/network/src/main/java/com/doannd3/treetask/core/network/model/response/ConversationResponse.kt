package com.doannd3.treetask.core.network.model.response

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class ConversationResponse(
    @SerialName("_id") val id: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("name") val name: String? = null,
    @SerialName("creator") val creator: String? = null,
    @SerialName("participants") val participants: List<ParticipantUser> = emptyList(),
    @SerialName("lastMessage") val lastMessage: LastMessage? = null,
    @SerialName("lastMessageAt") val lastMessageAt: String? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("__v") val version: Int? = null,
)

@Serializable
data class LastMessage(
    @SerialName("type") val type: String? = null,
)

@Serializable
data class ParticipantUser(
    @SerialName("_id") val id: String? = null,
    @SerialName("email") val email: String? = null,
    @SerialName("avatar") val avatar: String? = null,
    @SerialName("phone") val phone: String? = null,
)

@Serializable
data class ConversationsListResponse(
    @SerialName("conversations") val conversations: List<ConversationResponse> = emptyList(),
    @SerialName("totalItems") val totalItems: Int? = null,
    @SerialName("totalPages") val totalPages: Int? = null,
    @SerialName("currentPage") val currentPage: Int? = null,
)

@Serializable
data class MessageResponse(
    @SerialName("_id") val id: String? = null,
    @SerialName("conversationId") val conversationId: String? = null,
    @SerialName("senderId") val senderId: String? = null,
    @SerialName("content") val content: String? = null,
    @SerialName("type") val type: String? = null,
    @SerialName("createdAt") val createdAt: String? = null,
    @SerialName("updatedAt") val updatedAt: String? = null,
    @SerialName("__v") val version: Int? = null,
)
