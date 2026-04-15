package com.doannd3.treetask.feature.tasks.ui.home

import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.domain.usecase.task.TaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class TasksViewModel @Inject constructor(
    private val tasksUseCase: TaskUseCase
) : BaseViewModel(), MviViewModel<TasksState, TasksEvent, TasksEffect> {

    private val _uiState = MutableStateFlow(TasksState())
    override val uiState: StateFlow<TasksState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<TasksEffect>()
    override val effect: SharedFlow<TasksEffect> = _effect.asSharedFlow()

    init {
        getTasks()
    }

    override fun onEvent(event: TasksEvent) {
        when (event) {
            is TasksEvent.AddTaskClick -> {
                executeSafe {
                    _effect.emit(TasksEffect.ShowAddTask)
                }
            }

            is TasksEvent.SearchChanged -> {
                _uiState.update { it.copy(searchQuery = event.searchQuery) }
                applySearchAndFilter()
            }

            is TasksEvent.TaskClick -> {
                executeSafe {
                    _effect.emit(TasksEffect.ShowEditTask(task = event.task))
                }
            }

            is TasksEvent.FilterSelected -> {
                _uiState.update { it.copy(taskStatusSelected = event.taskStatusSelected) }
                applySearchAndFilter()
            }

            is TasksEvent.OnClearClick -> {
                _uiState.update { it.copy(searchQuery = "") }
            }
        }
    }

    private fun getTasks() {
        val status = uiState.value

        executeSafe {
            _uiState.update { it.copy(isLoading = true) }


            _uiState.update { it.copy(isLoading = false) }
        }
    }

    private fun applySearchAndFilter() {
        val state = uiState.value
        val queryFormatted = state.searchQuery.lowercase().trim()
        val status = state.taskStatusSelected

        val filteredList = state.allTasks.filter { task ->
            val matchSearch = task.title.lowercase().contains(
                queryFormatted
            )

            val matchStatus = if (status == null) {
                true
            } else {
                task.status == status
            }

            matchSearch && matchStatus
        }

        _uiState.update { it.copy(tasks = filteredList) }
    }
}