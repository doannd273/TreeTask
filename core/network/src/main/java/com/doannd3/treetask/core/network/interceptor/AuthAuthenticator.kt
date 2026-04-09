package com.doannd3.treetask.core.network.interceptor

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.datastore.TokenManager
import com.doannd3.treetask.core.network.model.request.RefreshTokenRequest
import com.doannd3.treetask.core.network.service.AuthService
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import timber.log.Timber
import javax.inject.Inject

class AuthAuthenticator @Inject constructor(
    private val tokenManager: TokenManager,
    private val authApi: Lazy<AuthService>
) : Authenticator {

    override fun authenticate(route: Route?, response: Response): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        synchronized(this) {
            val currentAccessToken = runBlocking { tokenManager.getAccessToken().first() }
            val originalToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            if (currentAccessToken != null && currentAccessToken != originalToken) {
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            try {
                val refreshToken = runBlocking {
                    tokenManager.getRefreshToken().first()
                } ?: return null

                val refreshResult = runBlocking {
                    authApi.get().refreshToken(RefreshTokenRequest(refreshToken = refreshToken))
                }

                val tokenData = when (refreshResult) {
                    is ApiResult.Success -> refreshResult.data
                    is ApiResult.Error -> {
                        runBlocking { tokenManager.clearToken() }
                        Timber.w("Refresh Token hết hạn hoặc lỗi: ${refreshResult.exception?.message}")
                        return null
                    }
                }
                val newAccessToken = tokenData.accessToken
                val newRefreshToken = tokenData.refreshToken

                runBlocking {
                    tokenManager.saveToken(newAccessToken, newRefreshToken)
                }

                // retry request cũ
                return response.request.newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            } catch (e: Exception) {
                // log lỗi
                Timber.e(e, "Lỗi kinh hoàng khi Refresh Token rớt mạng hoặc Server chết")

                runBlocking { tokenManager.clearToken() }
                return null
            }
        }
    }

    private fun responseCount(response: Response): Int {
        var result = 1
        var r = response.priorResponse

        while (r != null) {
            result++
            r = r.priorResponse
        }

        return result
    }
}