package com.doannd3.treetask.feature.chat.ui.conversation

import com.doannd3.treetask.core.common.UiText

data class ConversationState(
    val isLoading: Boolean = false,
)

sealed class ConversationEvent

sealed class ConversationEffect {
    data class ShowErrorMessage(val message: UiText) : ConversationEffect()
}
