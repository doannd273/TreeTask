package com.doannd3.treetask.core.domain.usecase.auth

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.doannd3.treetask.core.domain.validation.validationError
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): ApiResult<String> {
        val mailTrimmed = email.trim()
        if (mailTrimmed.isBlank()) {
            return validationError(R.string.common_error_email_empty)
        }

        return authRepository.forgotPassword(mailTrimmed)
    }
}
