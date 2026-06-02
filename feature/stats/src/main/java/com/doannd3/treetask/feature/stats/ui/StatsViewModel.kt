package com.doannd3.treetask.feature.stats.ui

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
class StatsViewModel
    @Inject
    constructor() :
    BaseViewModel(),
        MviViewModel<StatsState, StatsEvent, StatsEffect> {
        private val _uiState = MutableStateFlow(StatsState())
        override val uiState: StateFlow<StatsState> = _uiState.asStateFlow()

        private val _effect = MutableSharedFlow<StatsEffect>()
        override val effect: SharedFlow<StatsEffect> = _effect.asSharedFlow()

        override fun onEvent(event: StatsEvent) {
            // TODO
        }
    }
