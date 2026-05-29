package com.doannd3.treetask.core.domain.repository

interface PushTokenProvider {
    suspend fun getToken(): String?
}
