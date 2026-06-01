package com.doannd3.treetask.core.domain.usecase.user

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.doannd3.treetask.core.model.user.User
import javax.inject.Inject

class GetProfileUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        suspend operator fun invoke(): ApiResult<User> {
            return userRepository.getProfile()
        }
    }
