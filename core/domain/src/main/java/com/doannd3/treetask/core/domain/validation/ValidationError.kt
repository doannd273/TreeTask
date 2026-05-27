package com.doannd3.treetask.core.domain.validation

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.UiText

internal fun validationError(resId: Int): ApiResult.Error =
    ApiResult.Error(
        message = UiText.StringResource(resId),
        exception = null,
    )
