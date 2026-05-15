package com.doannd3.treetask.core.model.task

enum class TaskStatus(
    val apiValue: String,
    // TODO: Move UI label to presentation/resource mapper before localization support.
    val label: String,
) {
    TODO("todo", "Cần làm"),
    IN_PROGRESS("in_progress", "Đang thực hiện"),
    PENDING("pending", "Đang chờ"),
    DONE("done", "Hoàn thành"), ;

    companion object {
        fun fromStatus(status: String?): TaskStatus =
            entries.find { it.apiValue == status }
                ?: PENDING
    }
}
