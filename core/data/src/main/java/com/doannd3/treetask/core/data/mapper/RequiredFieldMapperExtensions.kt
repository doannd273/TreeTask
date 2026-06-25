package com.doannd3.treetask.core.data.mapper

import com.doannd3.treetask.core.common.extension.toInstantOrNull
import java.time.Instant

internal fun String?.requiredStringOrNull(): String? = this?.trim()?.takeIf { it.isNotEmpty() }

internal fun String?.optionalString(): String = this.orEmpty()

internal fun String?.requiredInstantOrNull(): Instant? = requiredStringOrNull()?.toInstantOrNull()
