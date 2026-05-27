package com.doannd3.treetask.feature.tasks.ui.add

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.task.CreateTaskUseCase
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.doannd3.treetask.core.common.R as CommonR
import com.doannd3.treetask.feature.tasks.R as TasksR

class AddTaskViewModel
@Inject
constructor(
    private val createTaskUseCase: CreateTaskUseCase,
) : BaseViewModel(),
    MviViewModel<AddTaskState, AddTaskEvent, AddTaskEffect> {
    private val _uiState = MutableStateFlow(AddTaskState())
    override val uiState: StateFlow<AddTaskState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<AddTaskEffect>()
    override val effect: SharedFlow<AddTaskEffect> = _effect.asSharedFlow()

    override fun onEvent(event: AddTaskEvent) {
        when (event) {
            is AddTaskEvent.TitleChanged -> {
                _uiState.update { it.copy(title = event.title) }
            }

            is AddTaskEvent.DescriptionChanged -> {
                _uiState.update { it.copy(description = event.description) }
            }

            is AddTaskEvent.DueDateChanged -> {
                _uiState.update { it.copy(dueDate = event.dueDate) }
            }

            is AddTaskEvent.StatusChanged -> {
                _uiState.update { it.copy(status = event.status) }
            }

            AddTaskEvent.SubmitAddTask -> {
                submitAddTask()
            }

            AddTaskEvent.SuccessAcknowledged -> {
                navigateBack()
            }

            AddTaskEvent.BackClicked -> {
                navigateBack()
            }
        }
    }

    private fun navigateBack() {
        viewModelScope.launch {
            _effect.emit(AddTaskEffect.NavigateBack)
        }
    }

    private fun submitAddTask() {
        val state = _uiState.value
        if (state.isLoading) {
            return
        }

        executeSafe {
            if (state.title.trim().isEmpty()) {
                _effect.emit(
                    AddTaskEffect.ShowErrorMessage(
                        UiText.StringResource(
                            TasksR.string.tasks_error_title_empty,
                        ),
                    ),
                )
                return@executeSafe
            }

            _uiState.update { it.copy(isLoading = true) }
            val result =
                createTaskUseCase(
                    title = state.title,
                    description = state.description,
                    status = state.status.apiValue,
                    dueDate = state.dueDate,
                )
            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is ApiResult.Success -> {
                    val message =
                        result.message
                            ?: UiText.StringResource(TasksR.string.tasks_add_task_success)
                    _effect.emit(AddTaskEffect.ShowSuccessMessage(message))
                }

                is ApiResult.Error -> {
                    val message =
                        result.toDisplayMessage(
                            UiText.StringResource(CommonR.string.common_error_unknown),
                        )
                    _effect.emit(AddTaskEffect.ShowErrorMessage(message))
                }
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}
