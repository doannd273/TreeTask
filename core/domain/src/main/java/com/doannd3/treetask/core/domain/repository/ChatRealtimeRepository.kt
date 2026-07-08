package com.doannd3.treetask.core.domain.repository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.model.chat.ChatRealtimeEvent
import kotlinx.coroutines.flow.Flow

interface ChatRealtimeRepository {
    fun observeEvents(): Flow<ChatRealtimeEvent>

    suspend fun connect(): ApiResult<Unit>

    suspend fun disconnect(): ApiResult<Unit>

    suspend fun joinConversation(conversationId: String): ApiResult<Unit>

    suspend fun leaveConversation(conversationId: String): ApiResult<Unit>
}
