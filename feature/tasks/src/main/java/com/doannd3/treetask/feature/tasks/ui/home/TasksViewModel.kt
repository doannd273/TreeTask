@file:OptIn(ExperimentalCoroutinesApi::class)

package com.doannd3.treetask.feature.tasks.ui.home

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.usecase.task.TaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.debounce
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.drop
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksUseCase: TaskUseCase
) : BaseViewModel(), MviViewModel<TasksState, TasksEvent, TasksEffect> {

    override fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading, isLoadingSearch = false) }
    }

    private val _uiState = MutableStateFlow(TasksState())
    override val uiState: StateFlow<TasksState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<TasksEffect>()
    override val effect: SharedFlow<TasksEffect> = _effect.asSharedFlow()

    init {
        initData()
        initObserver()
    }

    private fun initData() {
        getTasks(isInitialLoad = true)
            .launchSafeIn(viewModelScope)
    }

    private fun initObserver() {
        observeSearch()
        observeFilter()
    }

    override fun onEvent(event: TasksEvent) {
        when (event) {
            is TasksEvent.SearchChanged -> {
                _uiState.update { it.copy(searchQuery = event.searchQuery) }
            }

            is TasksEvent.FilterSelected -> {
                _uiState.update { it.copy(taskStatusSelected = event.taskStatusSelected) }
            }

            is TasksEvent.SearchQueryClear -> {
                _uiState.update { it.copy(searchQuery = "") }
            }
        }
    }

    private fun observeSearch() {
        uiState.map { state -> state.searchQuery }
            .drop(1)
            .distinctUntilChanged()
            .debounce(DELAY_SEARCH_TIME)
            .flatMapLatest {
                getTasks(isInitialLoad = false)
            }.launchSafeIn(viewModelScope)
    }

    private fun observeFilter() {
        uiState.map { it.taskStatusSelected }
            .drop(1)
            .distinctUntilChanged()
            .flatMapLatest {
                getTasks(isInitialLoad = false)
            }
            .launchSafeIn(viewModelScope)
    }

    private fun getTasks(isInitialLoad: Boolean) = flow {
        val state = uiState.value

        if (isInitialLoad) {
            _uiState.update { it.copy(isLoading = true) }
        } else {
            _uiState.update { it.copy(isLoadingSearch = true) }
        }
        val result = tasksUseCase.invoke(
            page = state.page,
            status = state.taskStatusSelected?.status ?: "",
            keyword = state.searchQuery
        )

        when (result) {
            is ApiResult.Success -> {
                _uiState.update {
                    it.copy(
                        tasks = result.data
                    )
                }
                emit(Unit)
            }

            is ApiResult.Error -> {
                val message =
                    result.message ?: UiText.StringResource(R.string.common_error_unknown)
                _effect.emit(TasksEffect.ShowErrorMessage(message))
                emit(Unit)
            }
        }
    }.onCompletion {
        _uiState.update { it.copy(isLoading = false, isLoadingSearch = false) }
    }

    companion object {
        const val DELAY_SEARCH_TIME = 500L
    }
}