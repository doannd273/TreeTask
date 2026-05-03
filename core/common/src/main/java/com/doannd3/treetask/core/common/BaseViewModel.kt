package com.doannd3.treetask.core.common

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.log.AppTag
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Job
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.launch
import timber.log.Timber

abstract class BaseViewModel : ViewModel() {
    // SharedFlow dùng chung để emit lỗi unexpected
    private val _baseErrorEffect = MutableSharedFlow<UiText>()
    val baseErrorEffect: SharedFlow<UiText> = _baseErrorEffect.asSharedFlow()

    private val exceptionHandler =
        CoroutineExceptionHandler { _, throwable ->
            Timber.tag(AppTag.COMMON).e(throwable, "Unhandled ViewModel Exception")
            throwable.printStackTrace()
            // Centralize tại đây: reset loading + emit error
            setLoading(false)
            viewModelScope.launch {
                _baseErrorEffect.emit(UiText.StringResource(R.string.common_error_unknown))
            }
        }

    // 3. Hàm launch mặc định (Dev chỉ việc dùng hàm này thay cho viewModelScope.launch)
    protected fun executeSafe(block: suspend CoroutineScope.() -> Unit) {
        viewModelScope.launch(exceptionHandler) {
            block()
        }
    }

    protected fun <T> Flow<T>.launchSafeIn(scope: CoroutineScope): Job {
        // Nhồi exceptionHandler của BaseViewModel vào để cản Crash
        return scope.launch(exceptionHandler) {
            collect() // Thu thập dòng chảy
        }
    }

    // Override nếu ViewModel có isLoading state
    // Không override = no-op (ViewModel không có loading)
    protected open fun setLoading(isLoading: Boolean) {}
}
