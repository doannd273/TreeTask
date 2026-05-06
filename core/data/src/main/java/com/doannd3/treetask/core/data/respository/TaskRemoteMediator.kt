package com.doannd3.treetask.core.data.respository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.data.model.toTaskDomain
import com.doannd3.treetask.core.data.model.toTaskEntity
import com.doannd3.treetask.core.database.dao.TaskDao
import com.doannd3.treetask.core.database.dao.TaskRemoteKeysDao
import com.doannd3.treetask.core.database.model.TaskEntity
import com.doannd3.treetask.core.database.model.TaskRemoteKeysEntity
import com.doannd3.treetask.core.network.service.TaskService
import javax.inject.Inject

@OptIn(ExperimentalPagingApi::class)
class TaskRemoteMediator(
    private val query: String,
    private val status: String,
    private val userId: String,
    private val taskRemoteKeysDao: TaskRemoteKeysDao,
    private val taskDao: TaskDao,
    private val taskService: TaskService,
) : RemoteMediator<Int, TaskEntity>() {
    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, TaskEntity>,
    ): MediatorResult {
        val page =
            when (loadType) {
                LoadType.REFRESH -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    remoteKeys?.nextKey?.minus(1) ?: 1
                }

                LoadType.PREPEND -> {
                    return MediatorResult.Success(endOfPaginationReached = true)
                }

                LoadType.APPEND -> {
                    val remoteKeys = getRemoteKeyForLastItem(state)
                    val nextKey =
                        remoteKeys?.nextKey ?: return MediatorResult.Success(
                            endOfPaginationReached = remoteKeys != null,
                        )
                    nextKey
                }
            }

        try {
            val apiResponse =
                taskService.getTasks(
                    page = page,
                    limit = state.config.pageSize,
                    status = status,
                    keyword = query,
                )
            return when (apiResponse) {
                is ApiResult.Success -> {
                    val tasks = apiResponse.data.tasks ?: emptyList()
                    val endOfPaginationReached = tasks.isEmpty()

                    if (loadType == LoadType.REFRESH) {
                        taskRemoteKeysDao.clearRemoteKeys()
                        taskDao.deleteTaskByUserId(userId)
                    }

                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1

                    val keys =
                        tasks.map {
                            TaskRemoteKeysEntity(
                                taskId = it.id ?: "",
                                preKey = prevKey,
                                nextKey = nextKey,
                            )
                        }

                    taskRemoteKeysDao.insertAll(keys)
                    taskDao.insertTasks(tasks.map { it.toTaskEntity() })

                    MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
                }

                is ApiResult.Error -> {
                    MediatorResult.Error(Exception(apiResponse.message))
                }
            }
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TaskEntity>): TaskRemoteKeysEntity? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { task ->
            taskRemoteKeysDao.remoteKeysTaskId(task.id)
        }

    private suspend fun getRemoteKeyClosestCurrentPosition(state: PagingState<Int, TaskEntity>): TaskRemoteKeysEntity? =
        state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { taskId ->
                taskRemoteKeysDao.remoteKeysTaskId(taskId)
            }
        }
}
