package com.doannd3.treetask.feature.chat.ui.detail

import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.chat.Message

data class ChatDetailState(
    val conversationId: String = "",
    val messages: List<Message> = emptyList(),
    val isLoading: Boolean = false,
    val isRefreshing: Boolean = false,
    val hasInitialLoadError: Boolean = false,
)

sealed class ChatDetailEvent {
    data class LoadMessages(val conversationId: String) : ChatDetailEvent()

    data object Refresh : ChatDetailEvent()

    data object BackClick : ChatDetailEvent()
}

sealed class ChatDetailEffect {
    data class ShowErrorMessage(val message: UiText) : ChatDetailEffect()

    data object NavigateBack : ChatDetailEffect()
}
