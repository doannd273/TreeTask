package com.doannd3.treetask.core.data.respository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.error.AppErrorCode
import com.doannd3.treetask.core.common.error.MissingResponseDataException
import com.doannd3.treetask.core.data.model.toTaskDomain
import com.doannd3.treetask.core.data.model.toTaskEntity
import com.doannd3.treetask.core.database.TreeTaskDatabase
import com.doannd3.treetask.core.database.dao.TaskDao
import com.doannd3.treetask.core.database.dao.TaskRemoteKeysDao
import com.doannd3.treetask.core.database.model.TaskRemoteKeysEntity
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.network.model.request.TaskRequest
import com.doannd3.treetask.core.network.service.TaskService
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import retrofit2.HttpException
import java.io.IOException
import javax.inject.Inject

class TaskRepositoryImpl
@Inject
constructor(
    private val taskService: TaskService,
    private val taskDao: TaskDao,
    private val taskRemoteKeysDao: TaskRemoteKeysDao,
    private val database: TreeTaskDatabase,
) : TaskRepository {
    @OptIn(ExperimentalPagingApi::class)
    override fun getTasks(
        status: String,
        keyword: String,
        userId: String,
    ): Flow<PagingData<Task>> {
        val pagingSourceFactory = { taskDao.getTasks(userId) }

        return Pager(
            config =
            PagingConfig(
                pageSize = LIMIT,
                prefetchDistance = 5,
                enablePlaceholders = false,
            ),
            remoteMediator =
            TaskRemoteMediator(
                query = keyword,
                status = status,
                userId = userId,
                taskService = taskService,
                database = database,
            ),
            pagingSourceFactory = pagingSourceFactory,
        ).flow.map { pagingData ->
            pagingData.map { it.toTaskDomain() }
        }
    }

    override suspend fun syncTasks(userId: String): ApiResult<Unit> =
        try {
            // Background sync: Lấy một lượng dữ liệu vừa đủ (ví dụ trang 1, 50 items)
            val apiResponse =
                taskService.getTasks(
                    page = 1,
                    limit = LIMIT,
                    status = "",
                    keyword = "",
                )

            when (apiResponse) {
                is ApiResult.Success -> {
                    val data =
                        apiResponse.data ?: return ApiResult.Error(
                            appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
                            exception = MissingResponseDataException(),
                        )
                    val tasks = data.tasks ?: emptyList()

                    // Cập nhật lại db như logic REFRESH của RemoteMediator trong một transaction
                    database.withTransaction {
                        taskRemoteKeysDao.clearRemoteKeys()
                        taskDao.deleteTaskByUserId(userId)

                        val keys =
                            tasks.map {
                                TaskRemoteKeysEntity(
                                    taskId = it.id ?: "",
                                    preKey = null,
                                    nextKey = if (tasks.isEmpty()) null else 2,
                                )
                            }

                        taskRemoteKeysDao.insertAll(keys)
                        taskDao.insertTasks(tasks.map { it.toTaskEntity() })
                    }

                    ApiResult.Success(data = Unit)
                }

                is ApiResult.Error -> {
                    apiResponse
                }
            }
        } catch (e: IOException) {
            ApiResult.Error(exception = e)
        } catch (e: HttpException) {
            ApiResult.Error(exception = e)
        }

    override suspend fun createTask(
        title: String,
        description: String,
        status: String,
        dueDate: String,
    ): ApiResult<Task> =
        try {
            val response =
                taskService.createTask(
                    TaskRequest(
                        title = title,
                        description = description,
                        status = status,
                        dueDate = dueDate,
                    ),
                )
            when (response) {
                is ApiResult.Success -> {
                    val taskResponse =
                        response.data ?: return ApiResult.Error(
                            appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
                            exception = MissingResponseDataException(),
                        )

                    val taskEntity = taskResponse.toTaskEntity()
                    taskDao.insertTasks(listOf(taskEntity))
                    ApiResult.Success(data = taskEntity.toTaskDomain())
                }

                is ApiResult.Error -> {
                    response
                }
            }
        } catch (e: IOException) {
            ApiResult.Error(exception = e)
        } catch (e: HttpException) {
            ApiResult.Error(exception = e)
        }

    override suspend fun updateTask(
        taskId: String,
        title: String,
        description: String,
        status: String,
        dueDate: String,
    ): ApiResult<Task> = try {
        val response = taskService.updateTask(
            taskId = taskId,
            request = TaskRequest(title = title, description = description, status = status, dueDate = dueDate),
        )
        when (response) {
            is ApiResult.Success -> {
                val taskResponse = response.data ?: return ApiResult.Error(
                    appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
                    exception = MissingResponseDataException(),
                )
                val taskEntity = taskResponse.toTaskEntity()
                taskDao.insertTasks(listOf(taskEntity))
                ApiResult.Success(data = taskEntity.toTaskDomain())
            }
            is ApiResult.Error -> response
        }
    } catch (e: IOException) {
        ApiResult.Error(exception = e)
    } catch (e: HttpException) {
        ApiResult.Error(exception = e)
    }

    override suspend fun getTaskById(taskId: String): ApiResult<Task> {
        val entity = taskDao.getTaskById(taskId = taskId) ?: return ApiResult.Error(
            appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
            exception = MissingResponseDataException(),
        )

        return ApiResult.Success(data = entity.toTaskDomain())
    }

    override suspend fun deleteTask(taskId: String) =
        try {
            val response = taskService.deleteTask(taskId = taskId)
            when (response) {
                is ApiResult.Success -> {
                    taskDao.deleteTaskById(taskId = taskId)
                    ApiResult.Success(data = Unit)
                }
                is ApiResult.Error -> response
            }
        } catch (e: IOException) {
            ApiResult.Error(exception = e)
        } catch (e: HttpException) {
            ApiResult.Error(exception = e)
        }

    companion object {
        const val LIMIT = 20
    }
}
