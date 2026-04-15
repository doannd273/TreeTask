package com.doannd3.treetask.feature.tasks.ui.home

import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus

data class TasksState(
    val isLoading: Boolean = false,
    val allTasks: List<Task> = emptyList(),
    val tasks: List<Task> = emptyList(), // hiện thị trên UI
    val searchQuery: String = "",
    val taskStatusSelected: TaskStatus? = null,
)

sealed class TasksEvent {
    data class SearchChanged(val searchQuery: String) : TasksEvent()
    data object OnClearClick : TasksEvent()
    data class FilterSelected(val taskStatusSelected: TaskStatus?) : TasksEvent()
    data class TaskClick(val task: Task) : TasksEvent()
    data object AddTaskClick : TasksEvent()
}

sealed class TasksEffect {
    data class ShowErrorMessage(val message: UiText) : TasksEffect()
    data class ShowEditTask(val task: Task) : TasksEffect()
    data object ShowAddTask: TasksEffect()
}