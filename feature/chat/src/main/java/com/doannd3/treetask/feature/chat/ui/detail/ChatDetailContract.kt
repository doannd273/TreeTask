package com.doannd3.treetask.feature.chat.ui.detail

import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.chat.Message

data class ChatDetailState(
    val conversationId: String = "",
    val currentUserId: String? = null,
    val messages: List<Message> = emptyList(),
    val draftMessage: String = "",
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val isSending: Boolean = false,
    val hasInitialLoadError: Boolean = false,
)

sealed class ChatDetailEvent {
    data class StartRealtime(val conversationId: String) : ChatDetailEvent()

    data class StopRealtime(val conversationId: String) : ChatDetailEvent()

    data class LoadMessages(val conversationId: String) : ChatDetailEvent()

    data object Refresh : ChatDetailEvent()

    data object BackClick : ChatDetailEvent()

    data class MessageChanged(val message: String) : ChatDetailEvent()

    data object SendMessageClicked : ChatDetailEvent()
}

sealed class ChatDetailEffect {
    data class ShowErrorMessage(val message: UiText) : ChatDetailEffect()

    data object NavigateBack : ChatDetailEffect()
}
