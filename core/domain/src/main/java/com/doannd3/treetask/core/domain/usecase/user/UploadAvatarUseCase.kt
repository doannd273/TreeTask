package com.doannd3.treetask.core.domain.usecase.user

import android.net.Uri
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.doannd3.treetask.core.domain.validation.validationError
import javax.inject.Inject

class UploadAvatarUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(uri: Uri): ApiResult<String> {
            if (uri.toString().isBlank()) {
                return validationError(R.string.common_error_avatar_empty)
            }

            return userRepository.uploadFile(uri = uri)
        }
    }
