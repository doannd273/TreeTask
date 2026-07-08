package com.doannd3.treetask.core.domain.validation

private const val OTP_LENGTH = 6
private val EMAIL_REGEX = Regex("^[A-Za-z0-9+_.-]+@[A-Za-z0-9.-]+\\.[A-Za-z]{2,}$")

internal fun String.isValidEmail(): Boolean = EMAIL_REGEX.matches(this)

internal fun String.isValidOtp(): Boolean = length == OTP_LENGTH && all { it.isDigit() }
