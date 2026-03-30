package com.doannd3.treetask.core.common

import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow

interface MviViewModel<State, Event, Effect> {
    val uiState: StateFlow<State>

    val effect: SharedFlow<Effect>

    fun onEvent(event: Event)
}