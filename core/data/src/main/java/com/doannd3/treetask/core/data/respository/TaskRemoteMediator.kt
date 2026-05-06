package com.doannd3.treetask.core.data.respository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.LoadType
import androidx.paging.PagingState
import androidx.paging.RemoteMediator
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.data.model.toTaskEntity
import com.doannd3.treetask.core.database.dao.TaskDao
import com.doannd3.treetask.core.database.dao.TaskRemoteKeysDao
import com.doannd3.treetask.core.database.model.TaskEntity
import com.doannd3.treetask.core.database.model.TaskRemoteKeysEntity
import com.doannd3.treetask.core.network.model.response.TaskResponse
import com.doannd3.treetask.core.network.service.TaskService
import retrofit2.HttpException
import java.io.IOException
import kotlin.collections.map

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
        val page = getPageToLoad(loadType, state) ?: return MediatorResult.Success(endOfPaginationReached = true)

        return try {
            val apiResponse =
                taskService.getTasks(
                    page = page,
                    limit = state.config.pageSize,
                    status = status,
                    keyword = query,
                )

            when (apiResponse) {
                is ApiResult.Success -> handleSuccessResponse(apiResponse.data.tasks, page, loadType)
                is ApiResult.Error -> MediatorResult.Error(Exception(apiResponse.message))
            }
        } catch (e: IOException) {
            MediatorResult.Error(e)
        } catch (e: HttpException) {
            MediatorResult.Error(e)
        }
    }

    private suspend fun getPageToLoad(
        loadType: LoadType,
        state: PagingState<Int, TaskEntity>,
    ): Int? =
        when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey?.minus(1) ?: 1
            }

            LoadType.PREPEND -> {
                null
            }

            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                remoteKeys?.nextKey
            }
        }

    private suspend fun handleSuccessResponse(
        networkTasks: List<TaskResponse>?,
        page: Int,
        loadType: LoadType,
    ): MediatorResult {
        val tasks = networkTasks ?: emptyList()
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

        return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, TaskEntity>): TaskRemoteKeysEntity? =
        state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { task ->
            taskRemoteKeysDao.remoteKeysTaskId(task.id)
        }
}
