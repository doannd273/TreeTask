package com.doannd3.treetask.core.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.doannd3.treetask.core.database.dao.TaskDao
import com.doannd3.treetask.core.database.model.TaskEntity

@Database(
    entities = [TaskEntity::class],
    version = 1,
    exportSchema = true
)
abstract class TreeTaskDatabase : RoomDatabase() {
    abstract fun taskDao(): TaskDao
}