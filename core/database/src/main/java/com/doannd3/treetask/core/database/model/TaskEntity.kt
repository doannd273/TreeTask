package com.doannd3.treetask.core.database.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey

/**
 * Bảng lưu trữ thông tin task
 */
@Entity(tableName = "tasks")
data class TaskEntity(
    @PrimaryKey
    @ColumnInfo(name = "id") val id: String,
    @ColumnInfo(name = "title") val title: String,
    @ColumnInfo(name = "description") val description: String?,
    @ColumnInfo(name = "status") val status: String,
    @ColumnInfo(name = "due_date") val dueDate: Long,
    @ColumnInfo(name = "created_at") val createdAt: Long,
    @ColumnInfo(name = "updated_at") val updatedAt: Long,
    @ColumnInfo(name = "user_id") val userId: String,
)

/**
 * Bảng lưu trữ xem task thuộc trang nào
 * trang tiếp theo và trước đó là trang nào
 */
@Entity(tableName = "task_remote_keys")
data class TaskRemoteKeysEntity(
    @PrimaryKey
    @ColumnInfo(name = "task_id") val taskId: String,
    @ColumnInfo(name = "pre_key") val preKey: Int?,
    @ColumnInfo(name = "next_key") val nextKey: Int?,
)
