package com.doannd3.treetask.feature.chat.ui.conversation

import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import javax.inject.Inject

@HiltViewModel
class ConversationViewModel
@Inject
constructor() :
    BaseViewModel(),
    MviViewModel<ConversationState, ConversationEvent, ConversationEffect> {
    private val _uiState = MutableStateFlow(ConversationState())
    override val uiState: StateFlow<ConversationState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ConversationEffect>()
    override val effect: SharedFlow<ConversationEffect> = _effect.asSharedFlow()

    override fun onEvent(event: ConversationEvent) {
        // TODO
    }
}
