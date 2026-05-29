package com.doannd3.treetask.core.domain.usecase.user

import com.doannd3.treetask.core.domain.repository.UserRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class ObserveCurrentUserIdUseCase
    @Inject
    constructor(
        private val userRepository: UserRepository,
    ) {
        operator fun invoke(): Flow<String> =
            userRepository
                .getCachedProfile()
                .map { user ->
                    user?.id.orEmpty()
                }.distinctUntilChanged()
    }
