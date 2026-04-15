package com.doannd3.treetask.feature.tasks.ui.home

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import java.time.Instant

@Composable
fun TasksScreen(
    state: TasksState,
    onSearchChange: (String) -> Unit,
    onClearClick: () -> Unit,
    onFilterSelect: (TaskStatus?) -> Unit,
    onTaskClick: (Task) -> Unit,
    onAddTaskClick: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        TasksContent(
            modifier = Modifier.padding(paddingValues = paddingValues),
            state = state,
            onSearchChange = onSearchChange,
            onClearClick = onClearClick,
            onFilterSelect = onFilterSelect,
            onTaskClick = onTaskClick,
            onAddTaskClick = onAddTaskClick,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun TasksScreenPreview() {
    val sampleTasksState = TasksState(
        isLoading = false,
        searchQuery = "",
        taskStatusSelected = TaskStatus.IN_PROGRESS,
        tasks = listOf(
            Task(
                id = "1",
                userId = "user_1",
                title = "Fix login bug",
                description = "Crash when login with Google",
                status = TaskStatus.PENDING,
                dueDate = Instant.parse("2026-04-20T10:00:00Z"),
                createdAt = Instant.parse("2026-04-10T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-15T09:00:00Z")
            ),
            Task(
                id = "2",
                userId = "user_1",
                title = "Design home screen",
                description = "Create UI with Compose",
                status = TaskStatus.TODO,
                dueDate = Instant.parse("2026-04-25T10:00:00Z"),
                createdAt = Instant.parse("2026-04-11T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-12T09:00:00Z")
            ),
            Task(
                id = "3",
                userId = "user_2",
                title = "Write unit test",
                description = null,
                status = TaskStatus.IN_PROGRESS,
                dueDate = Instant.parse("2026-04-18T10:00:00Z"),
                createdAt = Instant.parse("2026-04-09T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-14T09:00:00Z")
            ),
            Task(
                id = "4",
                userId = "user_2",
                title = "Fix payment issue",
                description = "Timeout when calling API",
                status = TaskStatus.DONE,
                dueDate = Instant.parse("2026-04-15T10:00:00Z"),
                createdAt = Instant.parse("2026-04-05T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-15T07:00:00Z")
            ),
            Task(
                id = "5",
                userId = "user_3",
                title = "Update profile UI",
                description = "Improve UX",
                status = TaskStatus.TODO,
                dueDate = Instant.parse("2026-04-28T10:00:00Z"),
                createdAt = Instant.parse("2026-04-13T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-13T09:00:00Z")
            ),
            Task(
                id = "6",
                userId = "user_1",
                title = "Fix crash on startup",
                description = "Android 14 issue",
                status = TaskStatus.IN_PROGRESS,
                dueDate = Instant.parse("2026-04-17T10:00:00Z"),
                createdAt = Instant.parse("2026-04-12T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-15T10:00:00Z")
            ),
            Task(
                id = "7",
                userId = "user_3",
                title = "Optimize RecyclerView",
                description = "Reduce lag when scrolling",
                status = TaskStatus.DONE,
                dueDate = Instant.parse("2026-04-22T10:00:00Z"),
                createdAt = Instant.parse("2026-04-01T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-10T09:00:00Z")
            ),
            Task(
                id = "8",
                userId = "user_2",
                title = "Implement search feature",
                description = "Search by title and description",
                status = TaskStatus.IN_PROGRESS,
                dueDate = Instant.parse("2026-04-19T10:00:00Z"),
                createdAt = Instant.parse("2026-04-08T08:00:00Z"),
                updatedAt = Instant.parse("2026-04-14T09:30:00Z")
            )
        )
    )

    TasksScreen(
        state = sampleTasksState,
        onSearchChange = {},
        onClearClick = {},
        onFilterSelect = {},
        onTaskClick = {},
        onAddTaskClick = {},
    )
}