package com.doannd3.treetask.core.domain.usecase.chat.realtime

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.ChatRealtimeRepository
import com.doannd3.treetask.core.domain.validation.validationError
import javax.inject.Inject

class StopChatConversationRealtimeUseCase @Inject constructor(
    private val chatRealtimeRepository: ChatRealtimeRepository,
) {
    suspend operator fun invoke(conversationId: String): ApiResult<Unit> {
        if (conversationId.isBlank()) {
            return validationError(R.string.common_error_conversation_id_empty)
        }

        val leaveResult =
            chatRealtimeRepository.leaveConversation(conversationId = conversationId)
        val disconnectResult = chatRealtimeRepository.disconnect()

        return when (leaveResult) {
            is ApiResult.Success -> disconnectResult
            is ApiResult.Error -> leaveResult
        }
    }
}
