package com.doannd3.treetask.feature.stats.ui

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.stats.GetTaskStatsUseCase
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
class StatsViewModel
    @Inject
    constructor(
        private val getTaskStatsUseCase: GetTaskStatsUseCase,
    ) : BaseViewModel(),
        MviViewModel<StatsState, StatsEvent, StatsEffect> {
        private val _uiState = MutableStateFlow(StatsState())
        override val uiState: StateFlow<StatsState> = _uiState.asStateFlow()

        private val _effect = MutableSharedFlow<StatsEffect>()
        override val effect: SharedFlow<StatsEffect> = _effect.asSharedFlow()

        override fun setLoading(isLoading: Boolean) {
            _uiState.update { it.copy(isLoading = isLoading) }
        }

        override fun onEvent(event: StatsEvent) {
            when (event) {
                StatsEvent.Refresh -> {
                    loadTaskStats()
                }
            }
        }

        init {
            loadTaskStats()
        }

        private fun loadTaskStats() {
            val state = _uiState.value
            if (state.isLoading) {
                return
            }

            _uiState.update { it.copy(isLoading = true) }

            executeSafe {
                val result = getTaskStatsUseCase()
                _uiState.update { it.copy(isLoading = false) }

                when (result) {
                    is ApiResult.Success -> {
                        _uiState.update {
                            it.copy(
                                hasInitialLoadError = false,
                                taskStats = result.data,
                            )
                        }
                    }

                    is ApiResult.Error -> {
                        if (state.taskStats == null) {
                            _uiState.update {
                                it.copy(
                                    hasInitialLoadError = true,
                                )
                            }
                        }

                        val message =
                            result.toDisplayMessage(
                                UiText.StringResource(CommonR.string.common_error_unknown),
                            )
                        _effect.emit(StatsEffect.ShowErrorMessage(message))
                    }
                }
            }
        }
    }
