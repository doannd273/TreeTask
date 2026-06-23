package com.doannd3.treetask.core.data.respository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import androidx.room.withTransaction
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.data.mapper.toTask
import com.doannd3.treetask.core.data.mapper.toTaskEntityOrNull
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
                pagingData.map { it.toTask() }
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

                apiResponse.mapSuccessResult { success ->
                    val data = success.data ?: return@mapSuccessResult missingResponseDataError()
                    val tasks = data.tasks ?: return@mapSuccessResult missingResponseDataError()
                    val taskEntities =
                        tasks.map { taskResponse ->
                            taskResponse.toTaskEntityOrNull()
                                ?: return@mapSuccessResult missingResponseDataError()
                        }

                    // Cập nhật lại db như logic REFRESH của RemoteMediator trong một transaction
                    database.withTransaction {
                        taskRemoteKeysDao.clearRemoteKeys()
                        taskDao.deleteTaskByUserId(userId)

                        val keys =
                            taskEntities.map { task ->
                                TaskRemoteKeysEntity(
                                    taskId = task.id,
                                    preKey = null,
                                    nextKey = if (taskEntities.isEmpty()) null else 2,
                                )
                            }

                        taskRemoteKeysDao.insertAll(keys)
                        taskDao.insertTasks(taskEntities)
                    }

                    ApiResult.Success(data = Unit)
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
                val request =
                    TaskRequest(
                        title = title,
                        description = description,
                        status = status,
                        dueDate = dueDate,
                    )

                val response = taskService.createTask(request = request)
                response.mapSuccessResult { success ->
                    val taskResponse =
                        success.data ?: return@mapSuccessResult missingResponseDataError()
                    val taskEntity =
                        taskResponse.toTaskEntityOrNull() ?: return@mapSuccessResult missingResponseDataError()

                    taskDao.insertTasks(listOf(taskEntity))
                    ApiResult.Success(data = taskEntity.toTask())
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
        ): ApiResult<Task> =
            try {
                val request =
                    TaskRequest(
                        title = title,
                        description = description,
                        status = status,
                        dueDate = dueDate,
                    )

                val response =
                    taskService.updateTask(
                        taskId = taskId,
                        request = request,
                    )
                response.mapSuccessResult { success ->
                    val taskResponse =
                        success.data ?: return@mapSuccessResult missingResponseDataError()
                    val taskEntity =
                        taskResponse.toTaskEntityOrNull() ?: return@mapSuccessResult missingResponseDataError()

                    taskDao.insertTasks(listOf(taskEntity))
                    ApiResult.Success(data = taskEntity.toTask())
                }
            } catch (e: IOException) {
                ApiResult.Error(exception = e)
            } catch (e: HttpException) {
                ApiResult.Error(exception = e)
            }

        override suspend fun getTaskById(taskId: String): ApiResult<Task> =
            try {
                val cachedTask = taskDao.getTaskById(taskId = taskId)
                if (cachedTask != null) {
                    ApiResult.Success(data = cachedTask.toTask())
                } else {
                    val response = taskService.getTaskById(taskId = taskId)
                    response.mapSuccessResult { success ->
                        val taskResponse =
                            success.data ?: return@mapSuccessResult missingResponseDataError()
                        val taskEntity =
                            taskResponse.toTaskEntityOrNull() ?: return@mapSuccessResult missingResponseDataError()

                        taskDao.insertTasks(listOf(taskEntity))
                        ApiResult.Success(data = taskEntity.toTask())
                    }
                }
            } catch (e: IOException) {
                ApiResult.Error(exception = e)
            } catch (e: HttpException) {
                ApiResult.Error(exception = e)
            }

        override suspend fun deleteTask(taskId: String) =
            try {
                val response = taskService.deleteTask(taskId = taskId)
                response.mapSuccessResult {
                    taskDao.deleteTaskById(taskId = taskId)
                    ApiResult.Success(data = Unit)
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
