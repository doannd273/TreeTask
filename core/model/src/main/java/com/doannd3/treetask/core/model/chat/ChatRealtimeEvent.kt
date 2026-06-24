package com.doannd3.treetask.core.model.chat

sealed interface ChatRealtimeEvent {
    data class NewMessage(
        val message: Message,
    ) : ChatRealtimeEvent

    data class TypingStarted(
        val conversationId: String,
        val userId: String,
    ) : ChatRealtimeEvent

    data class TypingStopped(
        val conversationId: String,
        val userId: String,
    ) : ChatRealtimeEvent
}
