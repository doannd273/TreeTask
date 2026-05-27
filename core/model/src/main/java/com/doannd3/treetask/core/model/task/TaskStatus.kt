package com.doannd3.treetask.core.model.task

enum class TaskStatus(
    val apiValue: String,
) {
    TODO("todo"),
    IN_PROGRESS("in_progress"),
    PENDING("pending"),
    DONE("done"),
    ;

    companion object {
        fun fromStatus(status: String?): TaskStatus =
            entries.find { it.apiValue == status }
                ?: PENDING
    }
}
