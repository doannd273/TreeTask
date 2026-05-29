package com.doannd3.treetask.core.common

import com.doannd3.treetask.core.common.error.AppErrorCode
import com.google.common.truth.Truth.assertThat
import org.junit.Test

class ApiResultTest {
    @Test
    fun `toDisplayMessage returns backend message first`() {
        val backendMessage = UiText.DynamicString("Backend error")
        val fallback = UiText.DynamicString("Fallback")

        val result =
            ApiResult.Error(
                message = backendMessage,
                appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
            ).toDisplayMessage(fallback)

        assertThat(result).isEqualTo(backendMessage)
    }

    @Test
    fun `toDisplayMessage returns app error message when backend message is missing`() {
        val fallback = UiText.DynamicString("Fallback")

        val result =
            ApiResult.Error(
                appErrorCode = AppErrorCode.MISSING_RESPONSE_DATA,
            ).toDisplayMessage(fallback)

        assertThat(result).isInstanceOf(UiText.StringResource::class.java)
        val stringResource = result as UiText.StringResource
        assertThat(stringResource.resId).isEqualTo(R.string.common_missing_response_data)
    }

    @Test
    fun `toDisplayMessage returns fallback when backend message and app error code are missing`() {
        val fallback = UiText.DynamicString("Fallback")

        val result =
            ApiResult.Error().toDisplayMessage(fallback)

        assertThat(result).isEqualTo(fallback)
    }
}
