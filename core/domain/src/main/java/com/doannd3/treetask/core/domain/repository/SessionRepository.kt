package com.doannd3.treetask.core.domain.repository

interface SessionRepository {
    suspend fun hasStoredSession(): Boolean
}
