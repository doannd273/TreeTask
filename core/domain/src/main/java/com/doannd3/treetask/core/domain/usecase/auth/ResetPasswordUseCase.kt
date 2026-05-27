package com.doannd3.treetask.core.domain.usecase.auth

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.doannd3.treetask.core.domain.validation.validationError
import javax.inject.Inject

class ResetPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(
        email: String,
        otp: String,
        newPassword: String,
    ): ApiResult<String> {
        val mailTrimmed = email.trim()
        if (mailTrimmed.isBlank()) {
            return validationError(R.string.common_error_email_empty)
        }

        val otpTrimmed = otp.trim()
        if (otpTrimmed.isBlank()) {
            return validationError(R.string.common_error_otp_empty)
        }

        val passwordTrimmed = newPassword.trim()
        if (passwordTrimmed.isBlank()) {
            return validationError(R.string.common_error_password_empty)
        }

        return authRepository.resetPassword(
            email = mailTrimmed,
            otp = otpTrimmed,
            newPassword = passwordTrimmed,
        )
    }
}
