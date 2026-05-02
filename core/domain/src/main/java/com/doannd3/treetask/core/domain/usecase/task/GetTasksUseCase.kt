package com.doannd3.treetask.core.domain.usecase.task

import com.doannd3.treetask.core.datastore.UserPrefsManager
import com.doannd3.treetask.core.domain.repository.TaskRepository
import com.doannd3.treetask.core.model.task.Task
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import javax.inject.Inject

class GetTasksUseCase @Inject constructor(
    private val tasksRepository: TaskRepository,
    private val userPrefManager: UserPrefsManager,
) {
    operator fun invoke(): Flow<List<Task>> {
        val userId = runBlocking {
            userPrefManager.getUserProfile().first()?.id.orEmpty()
        }
        return tasksRepository.getTasksStream(userId = userId)
    }
}
