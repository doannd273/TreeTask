package com.doannd3.treetask.core.domain.usecase.chat

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.ChatRepository
import com.doannd3.treetask.core.domain.validation.validationError
import com.doannd3.treetask.core.model.chat.Message
import javax.inject.Inject

class SendMessageUseCase
    @Inject
    constructor(
        private val chatRepository: ChatRepository,
    ) {
        suspend operator fun invoke(
            conversationId: String,
            content: String,
        ): ApiResult<Message> {
            val conversationIdTrimmed = conversationId.trim()
            if (conversationIdTrimmed.isBlank()) {
                return validationError(R.string.common_error_conversation_id_empty)
            }

            val contentTrimmed = content.trim()
            if (contentTrimmed.isBlank()) {
                return validationError(R.string.common_error_message_content_empty)
            }

            return chatRepository.sendMessage(
                conversationId = conversationIdTrimmed,
                content = contentTrimmed,
            )
        }
    }
