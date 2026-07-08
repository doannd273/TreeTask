package com.doannd3.treetask.core.domain.usecase.user

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.doannd3.treetask.core.domain.validation.validationError
import javax.inject.Inject

class ChangePasswordUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        currentPassword: String,
        newPassword: String,
    ): ApiResult<String> {
        val currentPasswordTrimmed = currentPassword.trim()
        if (currentPasswordTrimmed.isBlank()) {
            return validationError(R.string.common_error_password_empty)
        }

        val newPasswordTrimmed = newPassword.trim()
        if (newPasswordTrimmed.isBlank()) {
            return validationError(R.string.common_error_password_empty)
        }

        return userRepository.changePassword(
            currentPassword = currentPasswordTrimmed,
            newPassword = newPasswordTrimmed,
        )
    }
}
