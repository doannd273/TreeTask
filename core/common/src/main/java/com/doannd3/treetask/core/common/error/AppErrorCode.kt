package com.doannd3.treetask.core.common.error

import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText

enum class AppErrorCode {
    MISSING_RESPONSE_DATA,
    UNKNOWN,
}

fun AppErrorCode.toUiText(): UiText =
    when (this) {
        AppErrorCode.MISSING_RESPONSE_DATA ->
            UiText.StringResource(R.string.common_missing_response_data)

        AppErrorCode.UNKNOWN ->
            UiText.StringResource(R.string.common_error_unknown)
    }
