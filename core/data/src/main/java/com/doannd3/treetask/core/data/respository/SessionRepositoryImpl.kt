package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.datastore.token.TokenStorage
import com.doannd3.treetask.core.domain.repository.SessionRepository
import kotlinx.coroutines.flow.first
import javax.inject.Inject

class SessionRepositoryImpl
    @Inject
    constructor(
        private val tokenStorage: TokenStorage,
    ) : SessionRepository {
        override suspend fun hasStoredSession(): Boolean {
            return !tokenStorage.getAccessToken().first().isNullOrBlank()
        }
    }
