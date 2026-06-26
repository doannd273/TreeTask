package com.doannd3.treetask.feature.chat.ui.detail

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.chat.GetMessagesUseCase
import com.doannd3.treetask.core.domain.usecase.chat.SendMessageUseCase
import com.doannd3.treetask.core.domain.usecase.chat.realtime.ObserveChatRealtimeEventsUseCase
import com.doannd3.treetask.core.domain.usecase.chat.realtime.StartChatConversationRealtimeUseCase
import com.doannd3.treetask.core.domain.usecase.chat.realtime.StopChatConversationRealtimeUseCase
import com.doannd3.treetask.core.domain.usecase.user.ObserveCurrentUserIdUseCase
import com.doannd3.treetask.core.model.chat.ChatRealtimeEvent
import com.doannd3.treetask.core.model.chat.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.Job
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.doannd3.treetask.core.common.R as CommonR

@HiltViewModel
class ChatDetailViewModel
    @Inject
    constructor(
        private val observerUserIdUseCase: ObserveCurrentUserIdUseCase,
        private val getMessagesUseCase: GetMessagesUseCase,
        private val sendMessageUseCase: SendMessageUseCase,
        private val startChatConversationRealtimeUseCase: StartChatConversationRealtimeUseCase,
        private val stopChatConversationRealtimeUseCase: StopChatConversationRealtimeUseCase,
        private val observerChatRealtimeEventsUseCase: ObserveChatRealtimeEventsUseCase,
    ) : BaseViewModel(),
        MviViewModel<
            ChatDetailState,
            ChatDetailEvent,
            ChatDetailEffect,
        > {
        private val _uiState = MutableStateFlow(ChatDetailState())
        override val uiState: StateFlow<ChatDetailState> = _uiState.asStateFlow()

        private val _effect = MutableSharedFlow<ChatDetailEffect>()
        override val effect: SharedFlow<ChatDetailEffect> = _effect.asSharedFlow()

        private var activeRealtimeConversationId: String? = null
        private var realtimeStartJob: Job? = null
        private var realtimeStopJob: Job? = null

        init {
            observeCurrentUserId()
            observeRealtimeEvents()
        }

        private fun observeRealtimeEvents() {
            executeSafe {
                observerChatRealtimeEventsUseCase().collect { event ->
                    when (event) {
                        is ChatRealtimeEvent.NewMessage -> {
                            handleRealtimeNewMessage(event.message)
                        }

                        is ChatRealtimeEvent.TypingStarted -> {
                            handleTypingEvent(
                                conversationId = event.conversationId,
                                userId = event.userId,
                                isTyping = true,
                            )
                        }

                        is ChatRealtimeEvent.TypingStopped -> {
                            handleTypingEvent(
                                conversationId = event.conversationId,
                                userId = event.userId,
                                isTyping = false,
                            )
                        }
                    }
                }
            }
        }

        private fun handleTypingEvent(
            conversationId: String,
            userId: String,
            isTyping: Boolean,
        ) {
            _uiState.update { state ->
                if (
                    conversationId != state.conversationId ||
                    userId == state.currentUserId
                ) {
                    state
                } else if (isTyping) {
                    state.copy(typingUserId = userId)
                } else if (state.typingUserId == userId) {
                    state.copy(typingUserId = null)
                } else {
                    state
                }
            }
        }

        private fun handleRealtimeNewMessage(message: Message) {
            _uiState.update { state ->
                if (message.conversationId != state.conversationId) {
                    state
                } else {
                    state.copy(
                        messages = state.messages.upsertForDisplay(message = message),
                    )
                }
            }
        }

        private fun startRealtime(conversationId: String) {
            if (conversationId.isBlank()) {
                executeSafe {
                    _effect.emit(
                        ChatDetailEffect.ShowErrorMessage(
                            UiText.StringResource(CommonR.string.common_error_conversation_id_empty),
                        ),
                    )
                }
                return
            }

            if (activeRealtimeConversationId == conversationId) {
                return
            }

            activeRealtimeConversationId?.let { activeConversationId ->
                stopRealtime(activeConversationId)
            }

            val pendingStopJob = realtimeStopJob
            realtimeStartJob?.cancel()
            activeRealtimeConversationId = conversationId

            realtimeStartJob =
                viewModelScope.launch {
                    try {
                        pendingStopJob?.join()

                        when (val startResult = startChatConversationRealtimeUseCase(conversationId)) {
                            is ApiResult.Success -> {
                                Unit
                            }

                            is ApiResult.Error -> {
                                activeRealtimeConversationId = null
                                val message =
                                    startResult.toDisplayMessage(
                                        UiText.StringResource(CommonR.string.common_error_unknown),
                                    )
                                _effect.emit(ChatDetailEffect.ShowErrorMessage(message))
                            }
                        }
                    } catch (_: CancellationException) {
                        return@launch
                    } catch (_: Throwable) {
                        activeRealtimeConversationId = null
                        _effect.emit(
                            ChatDetailEffect.ShowErrorMessage(
                                UiText.StringResource(CommonR.string.common_error_unknown),
                            ),
                        )
                    }
                }
        }

        private fun stopRealtime(conversationId: String) {
            if (conversationId.isBlank()) {
                return
            }

            if (activeRealtimeConversationId != null &&
                activeRealtimeConversationId != conversationId
            ) {
                return
            }

            _uiState.update { it.copy(typingUserId = null) }

            val startJob = realtimeStartJob
            realtimeStartJob?.cancel()

            realtimeStopJob =
                viewModelScope.launch {
                    startJob?.cancelAndJoin()
                    if (activeRealtimeConversationId == conversationId) {
                        activeRealtimeConversationId = null
                    }

                    stopChatConversationRealtimeUseCase(conversationId)
                }
        }

        private fun observeCurrentUserId() {
            executeSafe {
                observerUserIdUseCase().collect { userId ->
                    _uiState.update { state ->
                        state.copy(
                            currentUserId = userId.takeIf { id -> id.isNotBlank() },
                        )
                    }
                }
            }
        }

        override fun onEvent(event: ChatDetailEvent) {
            when (event) {
                is ChatDetailEvent.LoadMessages -> {
                    loadMessages(
                        conversationId = event.conversationId,
                        isRefresh = false,
                    )
                }

                ChatDetailEvent.Refresh -> {
                    loadMessages(
                        conversationId = _uiState.value.conversationId,
                        isRefresh = true,
                    )
                }

                ChatDetailEvent.BackClick -> {
                    executeSafe {
                        _effect.emit(ChatDetailEffect.NavigateBack)
                    }
                }

                is ChatDetailEvent.MessageChanged -> {
                    _uiState.update {
                        it.copy(
                            draftMessage = event.message,
                        )
                    }
                }

                ChatDetailEvent.SendMessageClicked -> {
                    sendMessage()
                }

                is ChatDetailEvent.StartRealtime -> {
                    startRealtime(conversationId = event.conversationId)
                }

                is ChatDetailEvent.StopRealtime -> {
                    stopRealtime(conversationId = event.conversationId)
                }
            }
        }

        private fun loadMessages(
            conversationId: String,
            isRefresh: Boolean,
        ) {
            val state = _uiState.value
            if (state.isLoading || state.isRefreshing) {
                return
            }

            if (isRefresh) {
                _uiState.update {
                    it.copy(
                        conversationId = conversationId,
                        isRefreshing = true,
                        typingUserId = if (it.conversationId == conversationId) it.typingUserId else null,
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        conversationId = conversationId,
                        isLoading = true,
                        typingUserId = if (it.conversationId == conversationId) it.typingUserId else null,
                    )
                }
            }

            executeSafe {
                val result =
                    getMessagesUseCase(
                        conversationId = conversationId,
                        page = DEFAULT_PAGE,
                        limit = DEFAULT_LIMIT,
                    )

                _uiState.update {
                    it.copy(
                        isLoading = false,
                        isRefreshing = false,
                    )
                }

                when (result) {
                    is ApiResult.Success -> {
                        _uiState.update {
                            it.copy(
                                messages = result.data.orEmpty().toDisplayOrder(),
                                hasInitialLoadError = false,
                            )
                        }
                    }

                    is ApiResult.Error -> {
                        if (state.messages.isEmpty()) {
                            _uiState.update {
                                it.copy(hasInitialLoadError = true)
                            }
                        }

                        val message =
                            result.toDisplayMessage(
                                UiText.StringResource(CommonR.string.common_error_unknown),
                            )
                        _effect.emit(ChatDetailEffect.ShowErrorMessage(message))
                    }
                }
            }
        }

        private fun sendMessage() {
            val state = _uiState.value
            if (state.isSending) {
                return
            }

            val draftBeforeSend = state.draftMessage
            val content = draftBeforeSend.trim()
            if (content.isBlank()) {
                executeSafe {
                    _effect.emit(
                        ChatDetailEffect.ShowErrorMessage(
                            UiText.StringResource(CommonR.string.common_error_message_content_empty),
                        ),
                    )
                }
                return
            }

            executeSafe {
                _uiState.update {
                    it.copy(
                        isSending = true,
                    )
                }

                val result =
                    sendMessageUseCase(
                        conversationId = state.conversationId,
                        content = content,
                    )
                when (result) {
                    is ApiResult.Success -> {
                        val sendMessage = result.data
                        if (sendMessage == null) {
                            _uiState.update { it.copy(isSending = false) }
                            _effect.emit(
                                ChatDetailEffect.ShowErrorMessage(
                                    UiText.StringResource(CommonR.string.common_error_unknown),
                                ),
                            )
                            return@executeSafe
                        }

                        _uiState.update {
                            it.copy(
                                draftMessage = if (it.draftMessage == draftBeforeSend) "" else it.draftMessage,
                                messages = it.messages.upsertForDisplay(sendMessage),
                                isSending = false,
                                hasInitialLoadError = false,
                            )
                        }
                    }

                    is ApiResult.Error -> {
                        _uiState.update { it.copy(isSending = false) }

                        val message =
                            result.toDisplayMessage(
                                UiText.StringResource(CommonR.string.common_error_unknown),
                            )
                        _effect.emit(ChatDetailEffect.ShowErrorMessage(message))
                    }
                }
            }
        }

        override fun setLoading(isLoading: Boolean) {
            _uiState.update {
                it.copy(
                    isLoading = isLoading,
                    isRefreshing = if (!isLoading) false else it.isRefreshing,
                    isSending = if (!isLoading) false else it.isSending,
                )
            }
        }

        companion object {
            const val DEFAULT_PAGE = 1
            const val DEFAULT_LIMIT = 20
        }
    }

private fun List<Message>.toDisplayOrder(): List<Message> =
    distinctBy { it.id }
        .sortedWith(
            compareBy<Message> { it.createdAt }
                .thenBy { it.id },
        )

private fun List<Message>.upsertForDisplay(message: Message): List<Message> =
    filterNot { it.id == message.id }
        .plus(message)
        .toDisplayOrder()
