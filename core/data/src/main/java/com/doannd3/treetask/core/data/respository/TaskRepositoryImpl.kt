package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.dispatcher.Dispatcher
import com.doannd3.treetask.core.common.dispatcher.TreeTaskDispatchers
import com.doannd3.treetask.core.data.model.toTaskDomain
import com.doannd3.treetask.core.data.model.toTaskEntity
import com.doannd3.treetask.core.database.dao.TaskDao
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import com.doannd3.treetask.core.network.service.TaskService
import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.withContext
import javax.inject.Inject

class TaskRepositoryImpl
@Inject
constructor(
    private val taskService: TaskService,
    private val taskDao: TaskDao,
    @Dispatcher(TreeTaskDispatchers.IO) private val ioDispatcher: CoroutineDispatcher,
) : TaskRepository {
    override fun getTasksStream(userId: String): Flow<List<Task>> =
        taskDao.getTasks(userId = userId).map { taskEntities ->
            taskEntities.map { it.toTaskDomain() }
        }

    override suspend fun syncTasks(userId: String): ApiResult<Unit> {
        val result = taskService.getTasks(page = 1, limit = LIMIT, keyword = "", status = "")
        return when (result) {
            is ApiResult.Success -> {
                withContext(ioDispatcher) {
                    val tasks = result.data.tasks?.map { it.toTaskDomain() } ?: emptyList()
                    val entities = tasks.map { it.toTaskEntity(userId) }

                    taskDao.syncUserTasks(userId = userId, tasks = entities)
                }

                ApiResult.Success(Unit)
            }

            is ApiResult.Error -> {
                result
            }
        }
    }

    companion object {
        const val LIMIT = 10
    }
}
