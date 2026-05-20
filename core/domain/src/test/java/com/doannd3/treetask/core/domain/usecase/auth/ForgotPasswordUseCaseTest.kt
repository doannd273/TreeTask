package com.doannd3.treetask.core.domain.usecase.auth

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ForgotPasswordUseCaseTest {
    private lateinit var forgotPasswordUseCase: ForgotPasswordUseCase
    private val authRepository: AuthRepository = mockk()

    @Before
    fun setUp() {
        forgotPasswordUseCase = ForgotPasswordUseCase(authRepository)
    }

    @Test
    fun `forgot password with empty email returns email empty errors`() =
        runTest {
            // WHEN
            val result = forgotPasswordUseCase(email = "")

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Error::class.java)
            val errorResult = result as ApiResult.Error
            assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
            val stringResource = errorResult.message as UiText.StringResource
            assertThat(stringResource.resId).isEqualTo(R.string.common_error_email_empty)
            coVerify(exactly = 0) { authRepository.forgotPassword(any()) }
        }

    @Test
    fun `forgot password with valid input calls repository and returns success`() =
        runTest {
            // GIVEN
            val email = "test@treetask.com"
            coEvery { authRepository.forgotPassword(email) } returns
                ApiResult.Success<String>(message = UiText.DynamicString("Password reset email sent"))

            // WHEN
            val result = forgotPasswordUseCase(email = email)

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) { authRepository.forgotPassword(email) }
        }
}
