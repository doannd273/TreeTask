package com.doannd3.treetask.core.network.interceptor

import com.doannd3.treetask.core.common.log.AppTag
import com.doannd3.treetask.core.network.extensions.redact
import okhttp3.Interceptor
import okhttp3.Response
import timber.log.Timber
import java.io.IOException
import javax.inject.Inject

class NetworkDebugInterceptor
@Inject
constructor() : Interceptor {
    override fun intercept(chain: Interceptor.Chain): Response {
        val request = chain.request()

        val startTime = System.currentTimeMillis()

        // Log request
        Timber.tag(AppTag.NETWORK).d(
            """
                ➡️ REQUEST
                ${request.method} ${request.url}
                Headers: ${request.headers.redact()}
            """.trimIndent(),
        )

        return try {
            val response = chain.proceed(request)

            val duration = System.currentTimeMillis() - startTime

            // 👉 Log response
            Timber.tag(AppTag.NETWORK).d(
                """
                    ⬅️ RESPONSE (${duration}ms)
                    ${response.code} ${response.request.url}
                """.trimIndent(),
            )

            response
        } catch (e: IOException) {
            val duration = System.currentTimeMillis() - startTime

            Timber.tag(AppTag.NETWORK).e(
                e,
                """
                    ❌ ERROR (${duration}ms)
                    ${request.method} ${request.url}
                """.trimIndent(),
            )

            throw e
        }
    }
}
