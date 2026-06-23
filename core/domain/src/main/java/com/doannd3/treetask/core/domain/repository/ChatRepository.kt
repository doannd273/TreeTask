package com.doannd3.treetask.core.domain.repository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.model.chat.Conversation
import com.doannd3.treetask.core.model.chat.Message

interface ChatRepository {
    suspend fun getMessages(
        conversationId: String,
        page: Int,
        limit: Int,
    ): ApiResult<List<Message>>

    suspend fun getConversations(
        page: Int,
        limit: Int,
    ): ApiResult<List<Conversation>>

    suspend fun sendMessage(
        conversationId: String,
        content: String,
    ): ApiResult<Message>

    suspend fun createPrivateConversation(otherUserId: String): ApiResult<Conversation>
}
