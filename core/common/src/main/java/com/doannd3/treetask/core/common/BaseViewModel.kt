package com.doannd3.treetask.core.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel: ViewModel() {

    // 1. Tạo 1 Handler tập trung duy nhất
    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        // 1 Timber
        Timber.e(throwable, "Unhandled ViewModel Exception")
        // Ghi Log lên Firebase

        // 3.
        throwable.printStackTrace()
        // 4.
        handleUnexpectedError(throwable)
    }
    // 3. Hàm launch mặc định (Dev chỉ việc dùng hàm này thay cho viewModelScope.launch)
    protected fun executeSafe(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }

    // 4. Bắt buộc các màn hình con (ví dụ: AuthViewModel) phải quy định việc
    // lỡ như sập App thì hiển thị UI/Effect rớt mạng ra sao.
    protected abstract fun handleUnexpectedError(throwable: Throwable)
}