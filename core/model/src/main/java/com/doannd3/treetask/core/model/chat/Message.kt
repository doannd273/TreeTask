package com.doannd3.treetask.core.model.chat

import com.doannd3.treetask.core.model.user.User
import java.time.Instant

data class Message(
    val id: String,
    val conversationId: String,
    val sender: User,
    val content: String,
    val createdAt: Instant
)