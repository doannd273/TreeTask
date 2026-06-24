package com.doannd3.treetask.feature.chat.ui.detail

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.chat.GetMessagesUseCase
import com.doannd3.treetask.core.domain.usecase.chat.SendMessageUseCase
import com.doannd3.treetask.core.domain.usecase.user.ObserveCurrentUserIdUseCase
import com.doannd3.treetask.core.model.chat.Message
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject
import com.doannd3.treetask.core.common.R as CommonR

@HiltViewModel
class ChatDetailViewModel
    @Inject
    constructor(
        private val observerUserIdUseCase: ObserveCurrentUserIdUseCase,
        private val getMessagesUseCase: GetMessagesUseCase,
        private val sendMessageUseCase: SendMessageUseCase,
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

        init {
            observeCurrentUserId()
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
                    navigateBack()
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
                    )
                }
            } else {
                _uiState.update {
                    it.copy(
                        conversationId = conversationId,
                        isLoading = true,
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

        private fun navigateBack() {
            executeSafe {
                _effect.emit(ChatDetailEffect.NavigateBack)
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
