package com.doannd3.treetask.core.domain.repository

interface SessionRepository {
    suspend fun hasActiveSession(): Boolean
}
