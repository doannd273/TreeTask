package com.doannd3.treetask.core.domain.usecase.auth

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.doannd3.treetask.core.domain.validation.validationError
import javax.inject.Inject

class RegisterUseCase
    @Inject
    constructor(
        private val authRepository: AuthRepository,
    ) {
        suspend operator fun invoke(
            fullName: String,
            email: String,
            password: String,
        ): ApiResult<String> {
            val fullNameTrimmed = fullName.trim()
            if (fullNameTrimmed.isBlank()) {
                return validationError(R.string.common_error_fullName_empty)
            }

            val mailTrimmed = email.trim()
            if (mailTrimmed.isBlank()) {
                return validationError(R.string.common_error_email_empty)
            }

            val passwordTrimmed = password.trim()
            if (passwordTrimmed.isBlank()) {
                return validationError(R.string.common_error_password_empty)
            }

            return authRepository.register(fullNameTrimmed, mailTrimmed, passwordTrimmed)
        }
    }
