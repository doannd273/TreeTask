package com.doannd3.treetask.core.domain.usecase.device

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.DeviceRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class RegisterDeviceTokenUseCaseTest {
    private lateinit var registerDeviceTokenUseCase: RegisterDeviceTokenUseCase
    private val deviceRepository: DeviceRepository = mockk()

    @Before
    fun setUp() {
        registerDeviceTokenUseCase = RegisterDeviceTokenUseCase(deviceRepository)
    }

    @Test
    fun `blank token returns device token empty error`() =
        runTest {
            val result = registerDeviceTokenUseCase(token = "  ")

            assertStringResourceError(result, R.string.common_error_device_token_empty)
            coVerify(exactly = 0) { deviceRepository.registerToken(any()) }
        }

    @Test
    fun `valid token trims token and calls repository`() =
        runTest {
            coEvery {
                deviceRepository.registerToken(token = "device-token")
            } returns ApiResult.Success(data = Unit)

            val result = registerDeviceTokenUseCase(token = "  device-token  ")

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) { deviceRepository.registerToken(token = "device-token") }
        }

    private fun assertStringResourceError(
        result: ApiResult<*>,
        expectedResId: Int,
    ) {
        assertThat(result).isInstanceOf(ApiResult.Error::class.java)
        val errorResult = result as ApiResult.Error
        assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
        val stringResource = errorResult.message as UiText.StringResource
        assertThat(stringResource.resId).isEqualTo(expectedResId)
    }
}
