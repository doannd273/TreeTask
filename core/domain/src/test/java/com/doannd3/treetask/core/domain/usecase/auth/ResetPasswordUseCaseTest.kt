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

class ResetPasswordUseCaseTest {
    private lateinit var resetPasswordUseCase: ResetPasswordUseCase
    private val authRepository: AuthRepository = mockk()

    @Before
    fun setUp() {
        resetPasswordUseCase = ResetPasswordUseCase(authRepository)
    }

    @Test
    fun `reset password with empty email returns email empty error`() =
        runTest {
            // WHEN
            val result =
                resetPasswordUseCase(
                    email = "",
                    otp = "123456",
                    newPassword = "password123",
                )

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Error::class.java)
            val errorResult = result as ApiResult.Error
            assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
            val stringResource = errorResult.message as UiText.StringResource
            assertThat(stringResource.resId).isEqualTo(R.string.common_error_email_empty)
            coVerify(exactly = 0) { authRepository.resetPassword(any(), any(), any()) }
        }

    @Test
    fun `reset password with empty otp returns otp empty error`() =
        runTest {
            // WHEN
            val result =
                resetPasswordUseCase(
                    email = "test@treetask.com",
                    otp = "  ",
                    newPassword = "password123",
                )

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Error::class.java)
            val errorResult = result as ApiResult.Error
            assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
            val stringResource = errorResult.message as UiText.StringResource
            assertThat(stringResource.resId).isEqualTo(R.string.common_error_otp_empty)
            coVerify(exactly = 0) { authRepository.resetPassword(any(), any(), any()) }
        }

    @Test
    fun `reset password with empty password returns password empty error`() =
        runTest {
            // WHEN
            val result =
                resetPasswordUseCase(
                    email = "test@treetask.com",
                    otp = "123456",
                    newPassword = "  ",
                )

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Error::class.java)
            val errorResult = result as ApiResult.Error
            assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
            val stringResource = errorResult.message as UiText.StringResource
            assertThat(stringResource.resId).isEqualTo(R.string.common_error_password_empty)
            coVerify(exactly = 0) { authRepository.resetPassword(any(), any(), any()) }
        }

    @Test
    fun `reset password with valid input calls repository and returns success`() =
        runTest {
            // GIVEN
            val email = "test@treetask.com"
            val otp = "123456"
            val newPassword = "password123"
            coEvery {
                authRepository.resetPassword(email, otp, newPassword)
            } returns ApiResult.Success(message = UiText.DynamicString("Password reset"))

            // WHEN
            val result =
                resetPasswordUseCase(
                    email = email,
                    otp = otp,
                    newPassword = newPassword,
                )

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) { authRepository.resetPassword(email, otp, newPassword) }
        }

    @Test
    fun `reset password with valid input trims email otp and password before calling repository`() =
        runTest {
            // GIVEN
            val trimmedEmail = "test@treetask.com"
            val trimmedOtp = "123456"
            val trimmedPassword = "password123"
            coEvery {
                authRepository.resetPassword(trimmedEmail, trimmedOtp, trimmedPassword)
            } returns ApiResult.Success(message = UiText.DynamicString("Password reset"))

            // WHEN
            resetPasswordUseCase(
                email = "  test@treetask.com  ",
                otp = "  123456  ",
                newPassword = "  password123  ",
            )

            // THEN
            coVerify(exactly = 1) {
                authRepository.resetPassword(trimmedEmail, trimmedOtp, trimmedPassword)
            }
        }
}
