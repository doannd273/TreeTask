package com.doannd3.treetask.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.doannd3.treetask.core.database.dao.TaskDao
import com.doannd3.treetask.core.database.dao.TaskRemoteKeysDao
import com.doannd3.treetask.core.database.model.TaskEntity
import com.doannd3.treetask.core.database.model.TaskRemoteKeysEntity

@Database(
    entities = [TaskEntity::class, TaskRemoteKeysEntity::class],
    version = 1,
    exportSchema = true,
)
abstract class TreeTaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao

    abstract fun taskRemoteKeysDao(): TaskRemoteKeysDao
}
