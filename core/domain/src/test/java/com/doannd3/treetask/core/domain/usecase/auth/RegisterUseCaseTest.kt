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

class RegisterUseCaseTest {
    private lateinit var registerUseCase: RegisterUseCase
    private val authRepository: AuthRepository = mockk()

    @Before()
    fun setUp() {
        registerUseCase = RegisterUseCase(authRepository)
    }

    @Test
    fun `register with empty email returns email empty errors`() =
        runTest {
            // WHEN
            val result = registerUseCase(fullName = "Nguyen A", "", password = "123")

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Error::class.java)
            val errorResult = result as ApiResult.Error
            assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
            val stringResource = errorResult.message as UiText.StringResource
            assertThat(stringResource.resId).isEqualTo(R.string.common_error_email_empty)
        }

    @Test
    fun `register with empty full name returns full name empty errors`() =
        runTest {
            // WHEN
            val result =
                registerUseCase(fullName = "", email = "test@gmail.com", password = "123213")

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Error::class.java)
            val errorResult = result as ApiResult.Error
            assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
            val stringResource = errorResult.message as UiText.StringResource
            assertThat(stringResource.resId).isEqualTo(R.string.common_error_fullName_empty)
        }

    @Test
    fun `register with empty password returns password empty errors`() =
        runTest {
            // WHEN
            val result =
                registerUseCase(fullName = "TUAN", email = "test@gmail.com", password = "  ")

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Error::class.java)
            val errorResult = result as ApiResult.Error
            assertThat(errorResult.message).isInstanceOf(UiText.StringResource::class.java)
            val stringResource = errorResult.message as UiText.StringResource
            assertThat(stringResource.resId).isEqualTo(R.string.common_error_password_empty)
        }

    @Test
    fun `register with valid input call repository and return success`() =
        runTest {
            // GIVEN
            val fullName = "Nguyen Van A"
            val email = "test@treetask.com"
            val password = "password123"
            coEvery { authRepository.register(fullName, email, password) } returns ApiResult.Success(data = Unit)

            // WHEN
            val result = registerUseCase(fullName = fullName, email = email, password = password)

            // THEN
            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) { authRepository.register(fullName, email, password) }
        }

    @Test
    fun `register with valid input trim full name, email, password before calling repository`() =
        runTest {
            // GIVEN
            val fullName = "Nguyen Tuan   "
            val email = "  test@treetask.com  "
            val password = "  password123  "
            val trimmedEmail = "test@treetask.com"
            val trimmedPassword = "password123"
            val trimmedFullName = "Nguyen Tuan"

            coEvery {
                authRepository.register(trimmedFullName, trimmedEmail, trimmedPassword)
            } returns ApiResult.Success(data = Unit)

            // WHEN
            registerUseCase(fullName = fullName, email = email, password = password)

            // THEN
            coVerify(exactly = 1) { authRepository.register(trimmedFullName, trimmedEmail, trimmedPassword) }
        }
}
