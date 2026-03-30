package com.doannd3.treetask.core.domain.usecase.auth

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.AuthRepository
import javax.inject.Inject


class RegisterUseCase @Inject constructor(
    private val authRepository: AuthRepository
) {
    suspend operator fun invoke(email: String, password: String): ApiResult<Unit> {
        val mailTrimmed = email.trim()
        if (mailTrimmed.isBlank()) {
            return ApiResult.Error(
                message = UiText.StringResource(R.string.error_email_empty),
                exception = null
            )
        }

        val passwordTrimmed = password.trim()
        if (passwordTrimmed.isBlank()) {
            return ApiResult.Error(
                message = UiText.StringResource(R.string.error_password_empty),
                exception = null
            )
        }

        return authRepository.register(mailTrimmed, passwordTrimmed)
    }
}