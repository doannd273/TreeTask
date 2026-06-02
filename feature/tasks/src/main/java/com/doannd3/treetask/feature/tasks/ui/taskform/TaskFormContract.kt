package com.doannd3.treetask.feature.tasks.ui.taskform

import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.task.TaskStatus

enum class TaskFormMode {
    ADD,
    EDIT,
    VIEW,
    ;

    companion object {
        fun fromRouteValue(value: String?): TaskFormMode? = entries.firstOrNull { it.name == value }
    }
}

internal val TaskFormMode.isEditable: Boolean
    get() = this != TaskFormMode.VIEW

internal val TaskFormMode.isEdit: Boolean
    get() = this == TaskFormMode.EDIT

data class TaskFormState(
    val screenTitle: UiText? = null,
    val mode: TaskFormMode = TaskFormMode.ADD,
    val title: String = "",
    val description: String = "",
    val status: TaskStatus = TaskStatus.TODO,
    val dueDate: String = "",
    val isLoading: Boolean = false,
    val isLoadingTask: Boolean = false,
)

sealed class TaskFormEvent {
    data class DueDateChanged(
        val dueDate: String,
    ) : TaskFormEvent()

    data class TitleChanged(
        val title: String,
    ) : TaskFormEvent()

    data class DescriptionChanged(
        val description: String,
    ) : TaskFormEvent()

    data class StatusChanged(
        val status: TaskStatus,
    ) : TaskFormEvent()

    data object SubmitTaskForm : TaskFormEvent()

    data object SuccessAcknowledged : TaskFormEvent()

    data object BackClicked : TaskFormEvent()
}

sealed class TaskFormEffect {
    data object NavigateBack : TaskFormEffect()

    data class ShowSuccessMessage(
        val message: UiText,
    ) : TaskFormEffect()

    data class ShowErrorMessage(
        val message: UiText,
    ) : TaskFormEffect()
}
