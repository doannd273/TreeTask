package com.doannd3.treetask.core.data.respository

import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.map
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.dispatcher.Dispatcher
import com.doannd3.treetask.core.common.dispatcher.TreeTaskDispatchers
import com.doannd3.treetask.core.data.model.toTaskDomain
import com.doannd3.treetask.core.database.dao.TaskDao
import com.doannd3.treetask.core.database.dao.TaskRemoteKeysDao
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.network.service.TaskService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class TaskRepositoryImpl
    @Inject
    constructor(
        private val taskService: TaskService,
        private val taskDao: TaskDao,
        private val taskRemoteKeysDao: TaskRemoteKeysDao,
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
                        pageSize = 20,
                        prefetchDistance = 5, // Load trước khi scroll tới cuoi 5 item
                        enablePlaceholders = false,
                    ),
                remoteMediator =
                    TaskRemoteMediator(
                        query = keyword,
                        status = status,
                        userId = userId,
                        taskRemoteKeysDao = taskRemoteKeysDao,
                        taskDao = taskDao,
                        taskService = taskService,
                    ),
                pagingSourceFactory = pagingSourceFactory,
            ).flow.map { pagingData ->
                pagingData.map { it.toTaskDomain() }
            }
        }
    }
