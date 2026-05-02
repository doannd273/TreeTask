package com.doannd3.treetask.core.network.interceptor

import com.doannd3.treetask.core.datastore.TokenManager
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import okhttp3.Interceptor
import okhttp3.Request
import okhttp3.Response
import javax.inject.Inject

class AuthInterceptor
@Inject
constructor(
    private val tokenManager: TokenManager,
) : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val originalRequest = chain.request()

        val accessToken =
            runBlocking {
                tokenManager.getAccessToken().first()
            }
        if (accessToken.isNullOrEmpty()) {
            return chain.proceed(originalRequest)
        }

        val authRequest: Request =
            originalRequest.newBuilder()
                .header("Authorization", "Bearer $accessToken")
                .build()

        return chain.proceed(authRequest)
    }
}
