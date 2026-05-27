package com.doannd3.treetask.core.domain.usecase.task

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.model.task.TaskStatus
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test
import java.time.Instant

class CreateTaskUseCaseTest {

    private lateinit var createTaskUseCase: CreateTaskUseCase
    private val taskRepository: TaskRepository = mockk()

    @Before
    fun setUp() {
        createTaskUseCase = CreateTaskUseCase(taskRepository)
    }

    @Test
    fun `create task with empty title returns title empty error`() = runTest {
        val result = createTaskUseCase(
            title = "  ",
            description = "Description",
            status = TaskStatus.TODO.apiValue,
            dueDate = "2026-05-31",
        )

        assertStringResourceError(result, R.string.common_error_task_title_empty)
        coVerify(exactly = 0) { taskRepository.createTask(any(), any(), any(), any()) }
    }

    @Test
    fun `create task with empty description returns description empty error`() = runTest {
        val result = createTaskUseCase(
            title = "Title",
            description = "  ",
            status = TaskStatus.TODO.apiValue,
            dueDate = "2026-05-31",
        )

        assertStringResourceError(result, R.string.common_error_task_description_empty)
        coVerify(exactly = 0) { taskRepository.createTask(any(), any(), any(), any()) }
    }

    @Test
    fun `create task with empty status returns status empty error`() = runTest {
        val result = createTaskUseCase(
            title = "Title",
            description = "Description",
            status = "  ",
            dueDate = "2026-05-31",
        )

        assertStringResourceError(result, R.string.common_error_task_status_empty)
        coVerify(exactly = 0) { taskRepository.createTask(any(), any(), any(), any()) }
    }

    @Test
    fun `create task with invalid status returns invalid status error`() = runTest {
        val result = createTaskUseCase(
            title = "Title",
            description = "Description",
            status = "blocked",
            dueDate = "2026-05-31",
        )

        assertStringResourceError(result, R.string.common_error_task_status_invalid)
        coVerify(exactly = 0) { taskRepository.createTask(any(), any(), any(), any()) }
    }

    @Test
    fun `create task with empty due date returns due date empty error`() = runTest {
        val result = createTaskUseCase(
            title = "Title",
            description = "Description",
            status = TaskStatus.TODO.apiValue,
            dueDate = "  ",
        )

        assertStringResourceError(result, R.string.common_error_task_due_date_empty)
        coVerify(exactly = 0) { taskRepository.createTask(any(), any(), any(), any()) }
    }

    @Test
    fun `create task with invalid due date returns invalid due date error`() = runTest {
        val result = createTaskUseCase(
            title = "Title",
            description = "Description",
            status = TaskStatus.TODO.apiValue,
            dueDate = "2026-02-31",
        )

        assertStringResourceError(result, R.string.common_error_task_due_date_invalid)
        coVerify(exactly = 0) { taskRepository.createTask(any(), any(), any(), any()) }
    }

    @Test
    fun `create task with valid input trims fields and calls repository`() = runTest {
        val task = createTask()
        coEvery {
            taskRepository.createTask(
                title = "Title",
                description = "Description",
                status = TaskStatus.IN_PROGRESS.apiValue,
                dueDate = "2026-05-31",
            )
        } returns ApiResult.Success(data = task)

        val result = createTaskUseCase(
            title = "  Title  ",
            description = "  Description  ",
            status = "  ${TaskStatus.IN_PROGRESS.apiValue}  ",
            dueDate = "  2026-05-31  ",
        )

        assertThat(result).isInstanceOf(ApiResult.Success::class.java)
        coVerify(exactly = 1) {
            taskRepository.createTask(
                title = "Title",
                description = "Description",
                status = TaskStatus.IN_PROGRESS.apiValue,
                dueDate = "2026-05-31",
            )
        }
    }

    private fun assertStringResourceError(
        result: ApiResult<Task>,
        expectedResId: Int,
    ) {
        assertThat(result).isInstanceOf(ApiResult.Error::class.java)
        val errorResult = result as ApiResult.Error
        assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
        val stringResource = errorResult.message as UiText.StringResource
        assertThat(stringResource.resId).isEqualTo(expectedResId)
    }

    private fun createTask(): Task = Task(
        id = "task-id",
        userId = "user-id",
        title = "Title",
        description = "Description",
        status = TaskStatus.IN_PROGRESS,
        dueDate = Instant.parse("2026-05-31T00:00:00Z"),
        createdAt = Instant.parse("2026-05-01T00:00:00Z"),
        updatedAt = Instant.parse("2026-05-01T00:00:00Z"),
    )
}
