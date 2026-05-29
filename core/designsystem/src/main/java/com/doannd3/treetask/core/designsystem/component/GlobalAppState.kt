package com.doannd3.treetask.core.designsystem.component

import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import com.doannd3.treetask.core.designsystem.component.message.AppDialogState
import com.doannd3.treetask.core.designsystem.component.message.AppDialogType

@Stable
class GlobalAppState {
    // lưu trũ string báo lỗi
    var appDialogState by mutableStateOf<AppDialogState?>(null)
        private set

    fun showError(message: String?) {
        val errorDialogState =
            AppDialogState(
                type = AppDialogType.Error,
                message = message,
            )

        this@GlobalAppState.appDialogState = errorDialogState
    }

    fun showError(
        message: String?,
        title: String?,
    ) {
        val errorDialogState =
            AppDialogState(
                type = AppDialogType.Error,
                message = message,
                title = title,
            )

        this@GlobalAppState.appDialogState = errorDialogState
    }

    fun showSuccess(message: String?) {
        val errorDialogState =
            AppDialogState(
                type = AppDialogType.Success,
                message = message,
            )

        this@GlobalAppState.appDialogState = errorDialogState
    }

    fun showSuccess(
        message: String?,
        onDismiss: () -> Unit,
    ) {
        val errorDialogState =
            AppDialogState(
                type = AppDialogType.Success,
                message = message,
                onDismiss = onDismiss,
            )

        this@GlobalAppState.appDialogState = errorDialogState
    }

    fun showSuccess(
        message: String?,
        title: String?,
    ) {
        val errorDialogState =
            AppDialogState(
                type = AppDialogType.Success,
                message = message,
                title = title,
            )

        this@GlobalAppState.appDialogState = errorDialogState
    }

    fun clearDialog() {
        appDialogState = null
    }

    //
    var isGlobalLoading by mutableStateOf(false)
        private set

    fun showLoading() {
        isGlobalLoading = true
    }

    fun hideLoading() {
        isGlobalLoading = false
    }
}

// CHÚ Ý: Biến này phải NẰM NGOÀI class GlobalAppState
val LocalGlobalAppState =
    compositionLocalOf<GlobalAppState> {
        error("Vui lòng Provide GlobalAppState trước khi gọi LocalGlobalAppState.current")
    }
