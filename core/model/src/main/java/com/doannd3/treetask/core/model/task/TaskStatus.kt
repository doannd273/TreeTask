package com.doannd3.treetask.core.model.task

enum class TaskStatus(
    val apiValue: String,
    val label: String,
) {
    TODO("todo", "Cần làm"),
    IN_PROGRESS("in_progress", "Đang thực hiện"),
    PENDING("pending", "Đang chờ"),
    DONE("done", "Hoàn thành"),
    ;

    companion object {
        fun fromStatus(status: String?): TaskStatus =
            entries.find { it.apiValue == status }
                ?: PENDING
    }
}
