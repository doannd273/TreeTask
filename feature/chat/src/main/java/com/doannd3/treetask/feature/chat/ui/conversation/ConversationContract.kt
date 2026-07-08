package com.doannd3.treetask.feature.chat.ui.conversation

import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.chat.Conversation

data class ConversationState(
    val isLoading: Boolean = false,
    val conversations: List<Conversation> = emptyList(),
    val isRefreshing: Boolean = false,
    val hasInitialLoadError: Boolean = false,
)

sealed class ConversationEvent {
    data object LoadConversations : ConversationEvent()

    data object RefreshAfterReturnChatDetail : ConversationEvent()

    data object Refresh : ConversationEvent()

    data class ConversationClicked(val conversation: Conversation) : ConversationEvent()
}

sealed class ConversationEffect {
    data class ShowErrorMessage(val message: UiText) : ConversationEffect()

    data class NavigateToChatDetail(val conversationId: String) : ConversationEffect()
}
