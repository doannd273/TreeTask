package com.doannd3.treetask.core.network.util

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.UiText
import com.google.common.truth.Truth.assertThat
import io.mockk.every
import io.mockk.mockk
import kotlinx.serialization.json.Json
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.ResponseBody.Companion.toResponseBody
import org.junit.Test
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ApiResultCallTest {
    private val json = Json { ignoreUnknownKeys = true }

    @Test
    fun `error response maps backend code into backend error code`() {
        val errorBody =
            """
            {
              "success": false,
              "code": "AUTH_INVALID_CREDENTIALS",
              "message": "Invalid credentials"
            }
            """.trimIndent()
                .toResponseBody("application/json".toMediaType())

        val result = apiResultFor<String>(Response.error(401, errorBody))

        assertThat(result).isInstanceOf(ApiResult.Error::class.java)
        val error = result as ApiResult.Error
        assertThat(error.statusCode).isEqualTo(401)
        assertThat(error.backendErrorCode).isEqualTo("AUTH_INVALID_CREDENTIALS")
        assertThat((error.message as UiText.DynamicString).value).isEqualTo("Invalid credentials")
    }

    @Test
    fun `successful response preserves nullable data`() {
        val result =
            apiResultFor<String>(
                Response.success(
                    ApiResponse(
                        success = true,
                        message = "Email sent",
                        data = null,
                    ),
                ),
            )

        assertThat(result).isInstanceOf(ApiResult.Success::class.java)
        val success = result as ApiResult.Success
        assertThat(success.data).isNull()
        assertThat((success.message as UiText.DynamicString).value).isEqualTo("Email sent")
    }

    @Test
    fun `unsuccessful api body maps backend message and code without http error body`() {
        val result =
            apiResultFor<String>(
                Response.success(
                    ApiResponse(
                        success = false,
                        code = "EMAIL_ALREADY_EXISTS",
                        message = "Email already exists",
                        data = null,
                    ),
                ),
            )

        assertThat(result).isInstanceOf(ApiResult.Error::class.java)
        val error = result as ApiResult.Error
        assertThat(error.statusCode).isEqualTo(200)
        assertThat(error.backendErrorCode).isEqualTo("EMAIL_ALREADY_EXISTS")
        assertThat((error.message as UiText.DynamicString).value).isEqualTo("Email already exists")
    }

    private fun <T> apiResultFor(delegateResponse: Response<ApiResponse<T>>): ApiResult<T> {
        val delegate = mockk<Call<ApiResponse<T>>>(relaxed = true)
        every { delegate.enqueue(any()) } answers {
            firstArg<Callback<ApiResponse<T>>>().onResponse(delegate, delegateResponse)
        }

        return ApiResultCall(delegate, json).enqueueAndGet()
    }

    private fun <T> Call<ApiResult<T>>.enqueueAndGet(): ApiResult<T> {
        var result: ApiResult<T>? = null
        var failure: Throwable? = null

        enqueue(
            object : Callback<ApiResult<T>> {
                override fun onResponse(
                    call: Call<ApiResult<T>>,
                    response: Response<ApiResult<T>>,
                ) {
                    result = response.body()
                }

                override fun onFailure(
                    call: Call<ApiResult<T>>,
                    t: Throwable,
                ) {
                    failure = t
                }
            },
        )

        failure?.let { throw AssertionError("Unexpected callback failure", it) }
        return checkNotNull(result)
    }
}
