package com.doannd3.treetask.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo("id") val id: String,
    @ColumnInfo("title") val title: String,
    @ColumnInfo("description") val description: String,
    @ColumnInfo("status") val status: String,
    @ColumnInfo(name = "due_date") val dueDate: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "userId") val userId: String
)