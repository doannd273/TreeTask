package com.doannd3.treetask.core.data.mapper

import com.doannd3.treetask.core.common.extension.toInstantOrNull
import com.doannd3.treetask.core.model.chat.Conversation
import com.doannd3.treetask.core.model.chat.ConversationType
import com.doannd3.treetask.core.model.chat.Message
import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.core.network.model.response.ConversationResponse
import com.doannd3.treetask.core.network.model.response.LastMessage
import com.doannd3.treetask.core.network.model.response.MessageResponse
import com.doannd3.treetask.core.network.model.response.ParticipantUser

fun MessageResponse.toMessageOrNull(): Message? {
    val id = id.requiredStringOrNull() ?: return null
    val conversationId = conversationId.requiredStringOrNull() ?: return null
    val user = sender.toUserOrNull() ?: return null
    val messageType = type.requiredMessageTypeOrNull() ?: return null
    val content = content.requiredStringOrNull() ?: return null
    val createdAt = createdAt.requiredInstantOrNull() ?: return null

    return Message(
        id = id,
        conversationId = conversationId,
        user = user,
        type = messageType,
        content = content,
        createdAt = createdAt,
    )
}

fun ConversationResponse.toConversationOrNull(): Conversation? {
    val conversationId = id.requiredStringOrNull() ?: return null
    val creatorId = creator.requiredStringOrNull() ?: return null
    val conversationType = type.toConversationTypeOrNull() ?: return null
    val participantUsers =
        participants.mapOrNull { participant ->
            participant.toUserOrNull()
        } ?: return null
    val latestMessage = lastMessage?.toMessageOrNull(conversationId = conversationId)

    return Conversation(
        id = conversationId,
        type = conversationType,
        name = name.orEmpty(),
        creatorId = creatorId,
        participants = participantUsers,
        lastMessage = latestMessage,
        lastMessageAt = lastMessageAt.toInstantOrNull(),
    )
}

private fun LastMessage.toMessageOrNull(conversationId: String): Message? {
    val id = id.requiredStringOrNull() ?: return null
    val user = participantUser.toUserOrNull() ?: return null
    val messageType = type.requiredMessageTypeOrNull() ?: return null
    val content = content.requiredStringOrNull() ?: return null
    val createdAt = createdAt.requiredInstantOrNull() ?: return null

    return Message(
        id = id,
        conversationId = conversationId,
        user = user,
        type = messageType,
        content = content,
        createdAt = createdAt,
    )
}

private fun ParticipantUser?.toUserOrNull(): User? {
    val id = this?.id.requiredStringOrNull() ?: return null
    val email = this?.email.requiredStringOrNull() ?: return null
    val fullName = this?.fullName.requiredStringOrNull() ?: return null

    return User(
        id = id,
        email = email,
        fullName = fullName,
        avatar = this?.avatar,
        phone = this?.phone,
    )
}

private fun String?.toConversationTypeOrNull(): ConversationType? {
    return ConversationType.entries.firstOrNull { conversationType ->
        conversationType.type == this
    }
}
