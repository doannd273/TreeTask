package com.doannd3.treetask.core.common

import android.content.Context
import androidx.annotation.StringRes
import com.doannd3.treetask.core.common.R

sealed interface UiText {

    // backend, runtime
    data class DynamicString(
        val value: String?,
    ) : UiText

    // text từ strings.xml
    class StringResource(
        @StringRes val resId: Int,
        vararg val args: Any,
    ) : UiText
}

fun UiText.asString(context: Context): String {
    return when (this) {
        is UiText.DynamicString -> value ?: context.getString(R.string.common_error_unknown)

        is UiText.StringResource -> {
            return when {
                args.isEmpty() -> context.getString(resId)
                else -> context.getString(resId, *args)
            }
        }
    }
}
