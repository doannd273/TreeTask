package com.doannd3.treetask.core.domain.validation

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R

private const val MIN_PAGE = 1
private const val MIN_PAGE_SIZE = 1
private const val MAX_PAGE_SIZE = 100

internal fun validatePagination(
    page: Int,
    limit: Int,
): ApiResult.Error? {
    if (page < MIN_PAGE) {
        return validationError(R.string.common_error_page_invalid)
    }

    if (limit !in MIN_PAGE_SIZE..MAX_PAGE_SIZE) {
        return validationError(R.string.common_error_limit_invalid)
    }

    return null
}
