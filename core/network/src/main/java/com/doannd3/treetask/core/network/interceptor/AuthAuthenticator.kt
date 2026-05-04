package com.doannd3.treetask.core.network.interceptor

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.log.AppTag
import com.doannd3.treetask.core.datastore.token.TokenStorage
import com.doannd3.treetask.core.network.model.request.RefreshTokenRequest
import com.doannd3.treetask.core.network.service.AuthService
import dagger.Lazy
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.SerializationException
import okhttp3.Authenticator
import okhttp3.Request
import okhttp3.Response
import okhttp3.Route
import retrofit2.HttpException
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class AuthAuthenticator
@Inject
constructor(
    private val tokenStorage: TokenStorage,
    private val authApi: Lazy<AuthService>,
) : Authenticator {
    override fun authenticate(
        route: Route?,
        response: Response,
    ): Request? {
        if (responseCount(response) >= 2) {
            return null
        }

        synchronized(this) {
            val currentAccessToken = runBlocking { tokenStorage.getAccessToken().first() }
            val originalToken = response.request.header("Authorization")?.removePrefix("Bearer ")
            if (currentAccessToken != null && currentAccessToken != originalToken) {
                return response.request
                    .newBuilder()
                    .header("Authorization", "Bearer $currentAccessToken")
                    .build()
            }

            try {
                val refreshToken =
                    runBlocking {
                        tokenStorage.getRefreshToken().first()
                    } ?: return null

                val refreshResult =
                    runBlocking {
                        authApi.get().refreshToken(RefreshTokenRequest(refreshToken = refreshToken))
                    }

                val tokenData =
                    when (refreshResult) {
                        is ApiResult.Success -> {
                            refreshResult.data
                        }

                        is ApiResult.Error -> {
                            runBlocking { tokenStorage.clearToken() }
                            Timber.tag(AppTag.NETWORK).w("Refresh Token hết hạn hoặc lỗi: ${refreshResult.exception?.message}")
                            return null
                        }
                    }
                val newAccessToken = tokenData.accessToken
                val newRefreshToken = tokenData.refreshToken

                runBlocking {
                    tokenStorage.saveToken(newAccessToken, newRefreshToken)
                }

                // retry request cũ
                return response.request
                    .newBuilder()
                    .header("Authorization", "Bearer $newAccessToken")
                    .build()
            } catch (e: IOException) {
                // lỗi network
                Timber.tag(AppTag.NETWORK).e(e, "Network error khi refresh token")
            } catch (e: SerializationException) {
                // lỗi parse JSON
                Timber.tag(AppTag.NETWORK).e(e, "Parse error khi refresh token")
            } catch (e: HttpException) {
                // nếu bạn dùng Retrofit + coroutines adapter
                Timber.tag(AppTag.NETWORK).e(e, "HTTP error khi refresh token")
            }

            runBlocking { tokenStorage.clearToken() }
            return null
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
