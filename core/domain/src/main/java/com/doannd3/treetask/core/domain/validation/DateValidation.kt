package com.doannd3.treetask.core.domain.validation

import java.time.LocalDate

fun String.isValidYmdDate(): Boolean = runCatching { LocalDate.parse(this) }.isSuccess
