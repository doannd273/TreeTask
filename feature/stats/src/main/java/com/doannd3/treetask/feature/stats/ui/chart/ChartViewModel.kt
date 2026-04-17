package com.doannd3.treetask.feature.stats.ui.chart

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
class ChartViewModel @Inject constructor(

) : BaseViewModel(), MviViewModel<ChartState, ChartEvent, ChartEffect> {

    private val _uiState = MutableStateFlow(ChartState())
    override val uiState: StateFlow<ChartState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ChartEffect>()
    override val effect: SharedFlow<ChartEffect> = _effect.asSharedFlow()

    override fun onEvent(event: ChartEvent) {

    }
}