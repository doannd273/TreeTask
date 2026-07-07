package com.doannd3.treetask.core.domain.usecase.chat

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.ChatRepository
import com.doannd3.treetask.core.domain.validation.validationError
import com.doannd3.treetask.core.model.chat.Conversation
import javax.inject.Inject

class CreatePrivateConversationUseCase @Inject constructor(
    private val chatRepository: ChatRepository,
) {
    suspend operator fun invoke(otherUserId: String): ApiResult<Conversation> {
        val otherUserIdTrimmed = otherUserId.trim()
        if (otherUserIdTrimmed.isBlank()) {
            return validationError(R.string.common_error_other_user_id_empty)
        }

        return chatRepository.createPrivateConversation(otherUserId = otherUserIdTrimmed)
    }
}
