package com.doannd3.treetask.core.domain.usecase.chat

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.ChatRepository
import com.doannd3.treetask.core.domain.validation.validatePagination
import com.doannd3.treetask.core.model.chat.Conversation
import javax.inject.Inject

class GetConversationsUseCase
    @Inject
    constructor(
        private val chatRepository: ChatRepository,
    ) {
        suspend operator fun invoke(
            page: Int,
            limit: Int,
        ): ApiResult<List<Conversation>> {
            validatePagination(page = page, limit = limit)?.let { error ->
                return error
            }

            return chatRepository.getConversations(
                page = page,
                limit = limit,
            )
        }
    }
