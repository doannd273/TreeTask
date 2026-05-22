package com.doannd3.treetask.core.domain.usecase.user

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.doannd3.treetask.core.model.user.User
import javax.inject.Inject

class UpdateProfileUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    suspend operator fun invoke(
        fullName: String,
        phone: String,
        avatar: String,
    ): ApiResult<User> {
        val fullNameTrimmed = fullName.trim()
        if (fullNameTrimmed.isBlank()) {
            return ApiResult.Error(
                message = UiText.StringResource(R.string.common_error_fullName_empty),
                exception = null,
            )
        }

        return userRepository.updateProfile(
            fullName = fullNameTrimmed,
            phone = phone,
            avatar = avatar,
        )
    }
}
