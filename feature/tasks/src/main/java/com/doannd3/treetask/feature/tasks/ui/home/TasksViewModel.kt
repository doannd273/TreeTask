@file:OptIn(ExperimentalCoroutinesApi::class)

package com.doannd3.treetask.feature.tasks.ui.home

import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.task.DeleteTaskUseCase
import com.doannd3.treetask.core.domain.usecase.task.GetTasksUseCase
import com.doannd3.treetask.core.domain.usecase.user.ObserveCurrentUserIdUseCase
import com.doannd3.treetask.core.model.task.Task
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.flatMapLatest
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.doannd3.treetask.core.common.R as CommonR

@OptIn(FlowPreview::class)
@HiltViewModel
class TasksViewModel
    @Inject
    constructor(
        private val getTasksUseCase: GetTasksUseCase,
        private val deleteTaskUseCase: DeleteTaskUseCase,
        private val observeCurrentUserIdUseCase: ObserveCurrentUserIdUseCase,
    ) : BaseViewModel(),
        MviViewModel<TasksState, TasksEvent, TasksEffect> {
        override fun setLoading(isLoading: Boolean) {
            _uiState.update { it.copy(isLoading = isLoading) }
        }

        private val _uiState = MutableStateFlow(TasksState())
        override val uiState: StateFlow<TasksState> = _uiState.asStateFlow()

        private val _effect = MutableSharedFlow<TasksEffect>()
        override val effect: SharedFlow<TasksEffect> = _effect.asSharedFlow()

        private fun getPagingTasks(): Flow<PagingData<Task>> =
            uiState
                .map {
                    Triple(it.searchQuery, it.taskStatusSelected, it.userId)
                }.distinctUntilChanged()
                .flatMapLatest { (query, status, userId) ->
                    getTasksUseCase(
                        keyword = query,
                        status = status?.apiValue ?: "",
                        userId = userId,
                    )
                }.cachedIn(viewModelScope)

        init {
            // Giả sử bạn lấy userId từ storage khi init
            viewModelScope.launch {
                observeCurrentUserIdUseCase().collect { userId ->
                    _uiState.update { it.copy(userId = userId) }
                }
            }
            // Gán flow paging vào state
            _uiState.update { it.copy(tasks = getPagingTasks()) }
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

                TasksEvent.Refresh -> {
                }

                is TasksEvent.DeleteTask -> {
                    deleteTask(taskId = event.taskId)
                }
            }
        }

        private fun deleteTask(taskId: String) {
            executeSafe {
                _uiState.update { it.copy(isLoading = true) }
                val result = deleteTaskUseCase(taskId = taskId)
                _uiState.update { it.copy(isLoading = false) }
                when (result) {
                    is ApiResult.Success -> { }
                    is ApiResult.Error -> {
                        val message =
                            result.toDisplayMessage(
                                UiText.StringResource(CommonR.string.common_error_unknown),
                            )
                        _effect.emit(TasksEffect.ShowErrorMessage(message))
                    }
                }
            }
        }
    }
