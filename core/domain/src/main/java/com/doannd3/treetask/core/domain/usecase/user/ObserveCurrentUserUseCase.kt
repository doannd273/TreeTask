package com.doannd3.treetask.core.domain.usecase.user

import com.doannd3.treetask.core.domain.repository.UserRepository
import com.doannd3.treetask.core.model.user.User
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveCurrentUserUseCase @Inject constructor(
    private val userRepository: UserRepository,
) {
    operator fun invoke(): Flow<User?> {
        return userRepository.getCachedProfile()
    }
}
