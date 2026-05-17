package com.doannd3.treetask.core.domain.usecase.auth

import com.doannd3.treetask.core.domain.repository.AuthRepository
import javax.inject.Inject

class LogoutUseCase @Inject constructor(
    private val authRepository: AuthRepository,
) {
    suspend operator fun invoke() {
        return authRepository.logout()
    }
}
