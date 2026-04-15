package com.doannd3.treetask.feature.tasks.ui.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus

@Composable
fun TasksContent(
    modifier: Modifier = Modifier,
    state: TasksState,
    onSearchChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onFilterSelect: (TaskStatus?) -> Unit,
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center
    ) {
        SearchTaskInput(
            searchQuery = state.searchQuery,
            onSearchChange = onSearchChange,
            onClearClick = onClearClick,
        )

        TaskStatusChips(
            taskStatusSelected = state.taskStatusSelected,
            onFilterSelect = onFilterSelect
        )

        LazyColumn(
            modifier = Modifier.padding(top = 6.dp)
        ) {
            items(items = state.tasks, key = { task -> task.id }) { task ->
                TaskItem(task = task, onClick = {
                    onTaskClick.invoke(task)
                })
            }
        }
    }
}