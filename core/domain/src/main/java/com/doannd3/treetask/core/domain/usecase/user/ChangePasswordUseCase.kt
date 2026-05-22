package com.doannd3.treetask.core.domain.usecase.user

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.UserRepository
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
            return ApiResult.Error(
                message = UiText.StringResource(R.string.common_error_password_empty),
                exception = null,
            )
        }

        val newPasswordTrimmed = newPassword.trim()
        if (newPasswordTrimmed.isBlank()) {
            return ApiResult.Error(
                message = UiText.StringResource(R.string.common_error_password_empty),
                exception = null,
            )
        }

        return userRepository.changePassword(
            currentPassword = currentPasswordTrimmed,
            newPassword = newPasswordTrimmed,
        )
    }
}
