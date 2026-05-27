package com.doannd3.treetask.feature.tasks.ui.add

import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.task.TaskStatus

data class AddTaskState(
    val title: String = "",
    val description: String = "",
    val status: TaskStatus = TaskStatus.TODO,
    val dueDate: String = "",
    val isLoading: Boolean = false,
)

sealed class AddTaskEvent {
    data class TitleChanged(val title: String) : AddTaskEvent()

    data class DescriptionChanged(val description: String) : AddTaskEvent()

    data class StatusChanged(val status: TaskStatus) : AddTaskEvent()

    data class DueDateChanged(val dueDate: String) : AddTaskEvent()

    data object SubmitAddTask : AddTaskEvent()

    data object SuccessAcknowledged : AddTaskEvent()

    data object BackClicked : AddTaskEvent()
}

sealed class AddTaskEffect {
    data object NavigateBack : AddTaskEffect()

    data class ShowSuccessMessage(
        val message: UiText,
    ) : AddTaskEffect()

    data class ShowErrorMessage(
        val message: UiText,
    ) : AddTaskEffect()
}
