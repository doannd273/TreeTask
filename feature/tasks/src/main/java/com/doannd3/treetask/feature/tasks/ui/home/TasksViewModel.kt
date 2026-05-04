@file:OptIn(ExperimentalCoroutinesApi::class)

package com.doannd3.treetask.feature.tasks.ui.home

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.datastore.user.UserStorage
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.domain.usecase.task.GetTasksUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@OptIn(FlowPreview::class)
@HiltViewModel
class TasksViewModel
@Inject
constructor(
    private val getTasksUseCase: GetTasksUseCase,
    private val taskRepository: TaskRepository,
    private val userStorage: UserStorage,
) : BaseViewModel(),
    MviViewModel<TasksState, TasksEvent, TasksEffect> {
    override fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading, isLoadingSearch = false) }
    }

    private val _uiState = MutableStateFlow(TasksState())
    override val uiState: StateFlow<TasksState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<TasksEffect>()
    override val effect: SharedFlow<TasksEffect> = _effect.asSharedFlow()

    init {
        observeTasks()

        onEvent(TasksEvent.Refresh)
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
                syncTasks()
            }
        }
    }

    private fun observeTasks() {
        getTasksUseCase()
            .onEach { taskList ->
                _uiState.update { it.copy(tasks = taskList, isLoading = false) }
            }.launchSafeIn(viewModelScope)
    }

    private fun syncTasks() {
        userStorage
            .getUserProfile() // Flow<User>
            .onEach { user ->
                taskRepository.syncTasks(user?.id.orEmpty()) // Trả về Flow danh sách task của User đó
            }.launchSafeIn(viewModelScope)
    }
}
