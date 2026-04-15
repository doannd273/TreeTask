package com.doannd3.treetask.feature.tasks.ui.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.FilterChip
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.common.extension.toDayMonth
import com.doannd3.treetask.core.designsystem.theme.Black
import com.doannd3.treetask.core.designsystem.theme.Gray
import com.doannd3.treetask.core.designsystem.theme.Green
import com.doannd3.treetask.core.designsystem.theme.Purple40
import com.doannd3.treetask.core.designsystem.theme.Red10
import com.doannd3.treetask.core.designsystem.theme.White
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import com.doannd3.treetask.feature.tasks.R
import java.time.Instant

@Composable
fun TaskStatusChips(
    taskStatusSelected: TaskStatus?,
    onFilterSelect: (TaskStatus?) -> Unit,
) {
    val statusList = TaskStatus.entries.toTypedArray()

    LazyRow(
        modifier = Modifier.fillMaxWidth(),
        contentPadding = PaddingValues(end = 8.dp, top = 8.dp)
    ) {
        items(items = statusList) { taskStatus ->
            FilterChip(
                selected = taskStatusSelected == taskStatus,
                onClick = {
                    onFilterSelect(
                        if (taskStatusSelected == taskStatus) null else taskStatus
                    )
                },
                label = {
                    Text(text = taskStatus.status)
                },
                modifier = Modifier.padding(end = 8.dp)
            )
        }
    }
}

@Composable
fun SearchTaskInput(
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearClick: () -> Unit,
) {
    TextField(
        modifier = Modifier
            .fillMaxWidth()
            .border(
                width = 1.dp,
                color = Purple40,
                shape = RoundedCornerShape(6.dp)
            )
            .background(
                color = White,
                shape = RoundedCornerShape(6.dp)
            ),
        colors = TextFieldDefaults.colors(
            focusedTextColor = Black,
            unfocusedTextColor = Gray,

            focusedIndicatorColor = Color.Transparent,
            unfocusedIndicatorColor = Color.Transparent,
            disabledIndicatorColor = Color.Transparent,

            focusedContainerColor = Color.Transparent,
            unfocusedContainerColor = Color.Transparent,
            disabledContainerColor = Color.Transparent
        ),
        textStyle = TextStyle(
            fontSize = 15.sp
        ),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Done
        ),
        placeholder = {
            Text(text = stringResource(R.string.tasks_search_hint))
        },
        leadingIcon = {
            Icon(
                painterResource(R.drawable.tasks_ic_search),
                contentDescription = null
            )
        },
        trailingIcon = {
            IconButton(onClick = onClearClick) {
                Icon(
                    painterResource(R.drawable.tasks_ic_clear),
                    contentDescription = null
                )
            }
        },
        value = searchQuery,
        onValueChange = onSearchChange,
    )
}

@Composable
fun TaskItem(
    modifier: Modifier = Modifier,
    task: Task,
    onClick: () -> Unit
) {
    Card(
        onClick = onClick,
        modifier = modifier
            .fillMaxWidth()
            .padding(vertical = 6.dp),
        shape = RoundedCornerShape(3.dp),
        colors = CardDefaults.cardColors(containerColor = White),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)
    ) {
        Column(
            modifier = Modifier.padding(16.dp), verticalArrangement = Arrangement.spacedBy(8.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = task.title,
                    color = Purple40,
                    fontSize = 16.sp,
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis,
                    modifier = Modifier.weight(1f)
                )

                // Hiển thị Status Tag
                StatusBadge(status = task.status)
            }
            if (task.description?.isNotBlank() == true) {
                Text(
                    text = task.description ?: "",
                    color = Black,
                    fontSize = 13.sp,
                    maxLines = 2,
                    overflow = TextOverflow.Ellipsis
                )
            }
            Text(
                text = "Hạn chót: ${task.dueDate.toDayMonth()}",
                color = Black,
                fontSize = 13.sp,
            )
        }
    }
}

@Composable
fun StatusBadge(status: TaskStatus) {
    val backgroundColor = when (status) {
        TaskStatus.TODO -> Gray // Xám xanh
        TaskStatus.IN_PROGRESS -> Purple40 // Xanh dương
        TaskStatus.PENDING -> Red10 // Đỏ
        TaskStatus.DONE -> Green // Xanh lá
    }
    Row(
        modifier = Modifier
            .background(
                shape = RoundedCornerShape(3.dp),
                color = backgroundColor
            )
            .padding(vertical = 4.dp, horizontal = 8.dp)
    ) {
        Text(text = status.status, color = White, fontSize = 13.sp)
    }
}

@Composable
@Preview(showBackground = true)
fun TaskItemPreview() {
    TaskItem(
        task = Task(
            id = "1",
            userId = "user_1",
            title = "Fix login bug",
            description = "Crash when login with Google",
            status = TaskStatus.IN_PROGRESS,
            dueDate = Instant.parse("2026-04-20T10:00:00Z"),
            createdAt = Instant.parse("2026-04-10T08:00:00Z"),
            updatedAt = Instant.parse("2026-04-15T09:00:00Z")
        ),
        onClick = {},
    )
}

@Composable
@Preview(showBackground = true)
fun SearchTaskInputPreview() {
    SearchTaskInput(
        searchQuery = "",
        onSearchChange = {},
        onClearClick = {},
    )
}

@Preview(showBackground = true)
@Composable
fun PreviewTaskStatusChips() {
    var selected by remember { mutableStateOf(TaskStatus.TODO) }

    TaskStatusChips(
        taskStatusSelected = selected,
        onFilterSelect = {  }
    )
}