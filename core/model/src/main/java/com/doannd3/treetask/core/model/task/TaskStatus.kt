package com.doannd3.treetask.core.model.task

enum class TaskStatus(val status: String) {
    TODO("Cần làm"),
    IN_PROGRESS("Đang thực hiện"),
    PENDING("Đang chờ"),
    DONE("Hoàn thành"),
    ;

    companion object {
        fun fromStatus(status: String?): TaskStatus {
            return entries.find { it.status == status }
                ?: PENDING
        }
    }
}
