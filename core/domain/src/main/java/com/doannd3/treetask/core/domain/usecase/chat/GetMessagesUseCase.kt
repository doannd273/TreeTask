package com.doannd3.treetask.core.domain.usecase.chat

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.ChatRepository
import com.doannd3.treetask.core.domain.validation.validatePagination
import com.doannd3.treetask.core.domain.validation.validationError
import com.doannd3.treetask.core.model.chat.Message
import javax.inject.Inject

class GetMessagesUseCase
    @Inject
    constructor(
        private val chatRepository: ChatRepository,
    ) {
        suspend operator fun invoke(
            conversationId: String,
            page: Int,
            limit: Int,
        ): ApiResult<List<Message>> {
            if (conversationId.isBlank()) {
                return validationError(R.string.common_error_conversation_id_empty)
            }

            validatePagination(page = page, limit = limit)?.let { error ->
                return error
            }

            return chatRepository.getMessages(
                conversationId = conversationId,
                page = page,
                limit = limit,
            )
        }
    }
