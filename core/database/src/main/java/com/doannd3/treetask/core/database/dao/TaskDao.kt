package com.doannd3.treetask.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import com.doannd3.treetask.core.database.model.TaskEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface TaskDao {
    @Query("SELECT * FROM tasks WHERE user_id = :userId ORDER BY created_at DESC")
    fun getTasks(userId: String): Flow<List<TaskEntity>>

    @Query("SELECT * FROM tasks WHERE id = :taskId")
    suspend fun getTaskById(taskId: String): TaskEntity?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertTasks(tasks: List<TaskEntity>)

    @Query("DELETE FROM tasks WHERE id = :taskId")
    suspend fun deleteTaskById(taskId: String)

    @Query("DELETE FROM tasks WHERE user_id = :userId")
    suspend fun deleteTaskByUserId(userId: String)

    @Query("DELETE FROM tasks")
    suspend fun clearTasks()

    @Transaction
    suspend fun syncUserTasks(
        userId: String,
        tasks: List<TaskEntity>,
    ) {
        deleteTaskByUserId(userId = userId)
        insertTasks(tasks = tasks)
    }
}
