package com.doannd3.treetask.core.database.dao

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.doannd3.treetask.core.database.model.TaskRemoteKeysEntity

@Dao
interface TaskRemoteKeysDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(remoteKey: List<TaskRemoteKeysEntity>)

    @Query("SELECT * FROM task_remote_keys WHERE task_id = :taskId")
    suspend fun remoteKeysTaskId(taskId: String): TaskRemoteKeysEntity?

    @Query("DELETE FROM task_remote_keys")
    suspend fun clearRemoteKeys()
}
