package com.doannd3.treetask.core.domain.usecase.auth

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.AuthRepository
import javax.inject.Inject

class ForgotPasswordUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke(email: String): ApiResult<Unit> {
        val mailTrimmed = email.trim()
        if (mailTrimmed.isBlank()) {
            return ApiResult.Error(
                message = UiText.StringResource(R.string.common_error_email_empty),
                exception = null,
            )
        }

        return authRepository.forgotPassword(mailTrimmed)
    }
}
