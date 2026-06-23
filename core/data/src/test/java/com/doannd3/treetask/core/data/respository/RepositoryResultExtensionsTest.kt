package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.error.AppErrorCode
import com.doannd3.treetask.core.common.error.MissingResponseDataException
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class RepositoryResultExtensionsTest {
    @Test
    fun `mapSuccessResult maps success result and preserves message when caller returns it`() {
        val message = UiText.DynamicString("Created")
        val result =
            ApiResult.Success(
                data = "42",
                message = message,
            ).mapSuccessResult { success ->
                val data = success.data ?: return@mapSuccessResult missingResponseDataError()
                val mappedData =
                    data.toIntOrNull()
                        ?: return@mapSuccessResult missingResponseDataError()

                ApiResult.Success(
                    data = mappedData,
                    message = success.message,
                )
            }

        assertThat(result).isEqualTo(
            ApiResult.Success(
                data = 42,
                message = message,
            ),
        )
    }

    @Test
    fun `mapSuccessResult lets caller handle missing success data`() {
        val result =
            ApiResult.Success<String>(data = null).mapSuccessResult { success ->
                val data = success.data ?: return@mapSuccessResult missingResponseDataError()

                ApiResult.Success(data = data.length)
            }

        assertMissingResponseDataError(result)
    }

    @Test
    fun `mapSuccessResult lets caller handle invalid mapped data`() {
        val result =
            ApiResult.Success(data = "invalid").mapSuccessResult { success ->
                val data = success.data ?: return@mapSuccessResult missingResponseDataError()
                val mappedData =
                    data.toIntOrNull()
                        ?: return@mapSuccessResult missingResponseDataError()

                ApiResult.Success(data = mappedData)
            }

        assertMissingResponseDataError(result)
    }

    @Test
    fun `mapSuccessResult keeps error result unchanged`() {
        val error =
            ApiResult.Error(
                message = UiText.DynamicString("Invalid request"),
                statusCode = 400,
                backendErrorCode = "INVALID_REQUEST",
            )
        val source: ApiResult<String> = error

        val result =
            source.mapSuccessResult { success ->
                ApiResult.Success(data = success.data?.toIntOrNull())
            }

        assertThat(result).isSameInstanceAs(error)
    }

    private fun assertMissingResponseDataError(result: ApiResult<*>) {
        assertThat(result).isInstanceOf(ApiResult.Error::class.java)
        val error = result as ApiResult.Error
        assertThat(error.appErrorCode).isEqualTo(AppErrorCode.MISSING_RESPONSE_DATA)
        assertThat(error.exception).isInstanceOf(MissingResponseDataException::class.java)
    }
}
