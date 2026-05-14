package com.doannd3.treetask.core.designsystem.component.message

data class AppDialogState(
    val title: String? = null,
    val message: String?,
    val type: AppDialogType,
    val onDismiss: (() -> Unit)? = null,
)

enum class AppDialogType {
    Success,
    Error,
    Info,
}
