package com.doannd3.treetask.feature.tasks.ui.taskform

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.extension.toYmd
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.task.CreateTaskUseCase
import com.doannd3.treetask.core.domain.usecase.task.GetTaskByIdUseCase
import com.doannd3.treetask.core.domain.usecase.task.UpdateTaskUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
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

@HiltViewModel
class TaskFormViewModel
    @Inject
    constructor(
        savedStateHandle: SavedStateHandle,
        private val createTaskUseCase: CreateTaskUseCase,
        private val updateTaskUseCase: UpdateTaskUseCase,
        private val getTaskByIdUseCase: GetTaskByIdUseCase,
    ) : BaseViewModel(),
        MviViewModel<TaskFormState, TaskFormEvent, TaskFormEffect> {
        private val _uiState = MutableStateFlow(TaskFormState())
        override val uiState: StateFlow<TaskFormState> = _uiState.asStateFlow()

        private val _effect = MutableSharedFlow<TaskFormEffect>()
        override val effect: SharedFlow<TaskFormEffect> = _effect.asSharedFlow()

        private val taskId: String? = savedStateHandle["taskId"]
        private val mode = resolveMode(taskId = taskId, routeModeValue = savedStateHandle["mode"])

        init {
            _uiState.update {
                it.copy(
                    screenTitle = mode.screenTitle(),
                    mode = mode,
                )
            }

            if (taskId != null) {
                loadTask(taskId = taskId)
            }
        }

        private fun loadTask(taskId: String) {
            executeSafe {
                _uiState.update { it.copy(isLoadingTask = true) }
                when (val result = getTaskByIdUseCase(taskId)) {
                    is ApiResult.Success -> {
                        val task =
                            result.data ?: run {
                                _uiState.update { it.copy(isLoadingTask = false) }
                                _effect.emit(
                                    TaskFormEffect.ShowErrorMessage(
                                        UiText.StringResource(CommonR.string.common_error_unknown),
                                    ),
                                )
                                return@executeSafe
                            }
                        val dueDateString = task.dueDate.toYmd()
                        _uiState.update {
                            it.copy(
                                isLoadingTask = false,
                                title = task.title,
                                description = task.description.orEmpty(),
                                status = task.status,
                                dueDate = dueDateString,
                            )
                        }
                    }
                    is ApiResult.Error -> {
                        _uiState.update { it.copy(isLoadingTask = false) }
                        val message =
                            result.toDisplayMessage(
                                UiText.StringResource(CommonR.string.common_error_unknown),
                            )
                        _effect.emit(TaskFormEffect.ShowErrorMessage(message))
                    }
                }
            }
        }

        override fun onEvent(event: TaskFormEvent) {
            when (event) {
                is TaskFormEvent.TitleChanged -> {
                    updateIfEditable { it.copy(title = event.title) }
                }

                is TaskFormEvent.DescriptionChanged -> {
                    updateIfEditable { it.copy(description = event.description) }
                }

                is TaskFormEvent.StatusChanged -> {
                    updateIfEditable { it.copy(status = event.status) }
                }

                TaskFormEvent.SubmitTaskForm -> {
                    when (mode) {
                        TaskFormMode.ADD -> createTask()
                        TaskFormMode.EDIT -> updateTask()
                        TaskFormMode.VIEW -> Unit
                    }
                }

                TaskFormEvent.SuccessAcknowledged -> {
                    navigateBack()
                }

                TaskFormEvent.BackClicked -> {
                    navigateBack()
                }

                is TaskFormEvent.DueDateChanged -> {
                    updateIfEditable {
                        it.copy(dueDate = event.dueDate)
                    }
                }
            }
        }

        private inline fun updateIfEditable(transform: (TaskFormState) -> TaskFormState) {
            if (mode.isEditable) {
                _uiState.update(transform)
            }
        }

        private fun resolveMode(
            taskId: String?,
            routeModeValue: String?,
        ): TaskFormMode =
            when {
                taskId == null -> TaskFormMode.ADD
                else -> TaskFormMode.fromRouteValue(routeModeValue) ?: TaskFormMode.EDIT
            }

        private fun TaskFormMode.screenTitle(): UiText =
            UiText.StringResource(
                when (this) {
                    TaskFormMode.ADD -> TasksR.string.tasks_add_task_title
                    TaskFormMode.EDIT -> TasksR.string.tasks_edit_task_title
                    TaskFormMode.VIEW -> TasksR.string.tasks_task_details_title
                },
            )

        private fun navigateBack() {
            viewModelScope.launch {
                _effect.emit(TaskFormEffect.NavigateBack)
            }
        }

        private fun updateTask() {
            val state = _uiState.value
            if (state.isLoading) {
                return
            }

            executeSafe {
                _uiState.update { it.copy(isLoading = true) }
                val result =
                    updateTaskUseCase(
                        taskId = taskId!!,
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
                                ?: UiText.StringResource(TasksR.string.tasks_update_task_success)
                        _effect.emit(TaskFormEffect.ShowSuccessMessage(message))
                    }

                    is ApiResult.Error -> {
                        val message =
                            result.toDisplayMessage(
                                UiText.StringResource(CommonR.string.common_error_unknown),
                            )
                        _effect.emit(TaskFormEffect.ShowErrorMessage(message))
                    }
                }
            }
        }

        private fun createTask() {
            val state = _uiState.value
            if (state.isLoading) {
                return
            }

            executeSafe {
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
                        _effect.emit(TaskFormEffect.ShowSuccessMessage(message))
                    }

                    is ApiResult.Error -> {
                        val message =
                            result.toDisplayMessage(
                                UiText.StringResource(CommonR.string.common_error_unknown),
                            )
                        _effect.emit(TaskFormEffect.ShowErrorMessage(message))
                    }
                }
            }
        }

        override fun setLoading(isLoading: Boolean) {
            _uiState.update { it.copy(isLoading = isLoading) }
        }
    }
