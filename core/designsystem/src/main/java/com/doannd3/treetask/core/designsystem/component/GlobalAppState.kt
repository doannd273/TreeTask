package com.doannd3.treetask.core.designsystem.component

import androidx.compose.runtime.Stable
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue

@Stable
class GlobalAppState {
    // lưu trũ string báo lỗi
    var errorMessage by mutableStateOf<String?>(null)
        private set

    fun showError(message: String?) {
        errorMessage = message
    }

    fun clearError() {
        errorMessage = null
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
val LocalGlobalAppState = compositionLocalOf<GlobalAppState> {
    error("Vui lòng Provide GlobalAppState trước khi gọi LocalGlobalAppState.current")
}
