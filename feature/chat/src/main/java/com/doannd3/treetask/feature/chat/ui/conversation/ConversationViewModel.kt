package com.doannd3.treetask.feature.chat.ui.conversation

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.chat.GetConversationsUseCase
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
class ConversationViewModel @Inject constructor(
    private val getConversationsUseCase: GetConversationsUseCase,
) : BaseViewModel(),
    MviViewModel<ConversationState, ConversationEvent, ConversationEffect> {
    private val _uiState = MutableStateFlow(ConversationState())
    override val uiState: StateFlow<ConversationState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ConversationEffect>()
    override val effect: SharedFlow<ConversationEffect> = _effect.asSharedFlow()

    override fun onEvent(event: ConversationEvent) {
        when (event) {
            ConversationEvent.LoadConversations -> {
                loadConversation(isRefresh = false)
            }

            ConversationEvent.Refresh -> {
                loadConversation(isRefresh = true)
            }

            is ConversationEvent.ConversationClicked -> {
                navigateToConversation(event.conversation.id)
            }

            ConversationEvent.RefreshAfterReturnChatDetail -> {
                loadConversation(isRefresh = true)
            }
        }
    }

    private fun navigateToConversation(conversationId: String) {
        executeSafe {
            _effect.emit(ConversationEffect.NavigateToChatDetail(conversationId))
        }
    }

    private fun loadConversation(isRefresh: Boolean) {
        val uiState = _uiState.value
        if (uiState.isLoading || uiState.isRefreshing) {
            return
        }

        if (isRefresh) {
            _uiState.update { it.copy(isRefreshing = true) }
        } else {
            _uiState.update { it.copy(isLoading = true) }
        }

        executeSafe {
            val result =
                getConversationsUseCase(
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
                        it.copy(conversations = result.data.orEmpty(), hasInitialLoadError = false)
                    }
                }

                is ApiResult.Error -> {
                    if (uiState.conversations.isEmpty()) {
                        _uiState.update {
                            it.copy(hasInitialLoadError = true)
                        }
                    }

                    val message =
                        result.toDisplayMessage(
                            UiText.StringResource(CommonR.string.common_error_unknown),
                        )
                    _effect.emit(ConversationEffect.ShowErrorMessage(message))
                }
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        _uiState.update {
            it.copy(
                isLoading = isLoading,
                isRefreshing = if (!isLoading) false else it.isRefreshing,
            )
        }
    }

    companion object {
        const val DEFAULT_PAGE = 1
        const val DEFAULT_LIMIT = 20
    }
}
