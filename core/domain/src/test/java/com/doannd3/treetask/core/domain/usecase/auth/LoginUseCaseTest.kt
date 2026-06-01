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

class LoginUseCaseTest {
    private lateinit var loginUseCase: LoginUseCase
    private val authRepository: AuthRepository = mockk()

    @Before
    fun setUp() {
        loginUseCase = LoginUseCase(authRepository)
    }

    @Test
    fun `login with empty email returns email empty error`() =
        runTest {
            // WHEN
            val result = loginUseCase(email = "", password = "password123")

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Error::class.java)
            val errorResult = result as ApiResult.Error
            assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
            val stringResource = errorResult.message as UiText.StringResource
            assertThat(stringResource.resId).isEqualTo(R.string.common_error_email_empty)
            coVerify(exactly = 0) { authRepository.login(any(), any()) }
        }

    @Test
    fun `login with empty password returns password empty error`() =
        runTest {
            // WHEN
            val result = loginUseCase(email = "test@treetask.com", password = "  ")

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Error::class.java)
            val errorResult = result as ApiResult.Error
            assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
            val stringResource = errorResult.message as UiText.StringResource
            assertThat(stringResource.resId).isEqualTo(R.string.common_error_password_empty)
            coVerify(exactly = 0) { authRepository.login(any(), any()) }
        }

    @Test
    fun `login with valid input calls repository and returns success`() =
        runTest {
            // GIVEN
            val email = "test@treetask.com"
            val password = "password123"
            coEvery { authRepository.login(email, password) } returns ApiResult.Success(data = Unit)

            // WHEN
            val result = loginUseCase(email = email, password = password)

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) { authRepository.login(email, password) }
        }

    @Test
    fun `login with valid input trims email and password before calling repository`() =
        runTest {
            // GIVEN
            val email = "  test@treetask.com  "
            val password = "  password123  "
            val trimmedEmail = "test@treetask.com"
            val trimmedPassword = "password123"

            coEvery { authRepository.login(trimmedEmail, trimmedPassword) } returns ApiResult.Success(data = Unit)

            // WHEN
            loginUseCase(email = email, password = password)

            // THEN
            coVerify(exactly = 1) { authRepository.login(trimmedEmail, trimmedPassword) }
        }
}
