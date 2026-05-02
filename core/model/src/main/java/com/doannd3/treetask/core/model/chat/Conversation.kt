package com.doannd3.treetask.core.model.chat

import com.doannd3.treetask.core.model.user.User
import java.time.Instant

data class Conversation(
    val id: String,
    val type: ConversationType,
    val name: String,
    val creatorId: String,
    val participants: List<User>,
    val lastMessage: Message?,
    val lastMessageAt: Instant?,
)
