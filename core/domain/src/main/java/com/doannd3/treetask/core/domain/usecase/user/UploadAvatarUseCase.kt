package com.doannd3.treetask.core.domain.usecase.user

import android.net.Uri
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.UserRepository
import javax.inject.Inject

class UploadAvatarUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(uri: Uri): ApiResult<String> {
            return userRepository.uploadFile(uri = uri)
        }
    }
