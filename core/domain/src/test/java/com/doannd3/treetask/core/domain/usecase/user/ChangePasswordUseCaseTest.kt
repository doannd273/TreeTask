package com.doannd3.treetask.core.domain.usecase.user

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class ChangePasswordUseCaseTest {
    private lateinit var changePasswordUseCase: ChangePasswordUseCase
    private val userRepository: UserRepository = mockk()

    @Before
    fun setUp() {
        changePasswordUseCase = ChangePasswordUseCase(userRepository)
    }

    @Test
    fun `blank current password returns password empty error`() =
        runTest {
            val result =
                changePasswordUseCase(
                    currentPassword = "  ",
                    newPassword = "password123",
                )

            assertStringResourceError(result, R.string.common_error_password_empty)
            coVerify(exactly = 0) { userRepository.changePassword(any(), any()) }
        }

    @Test
    fun `blank new password returns password empty error`() =
        runTest {
            val result =
                changePasswordUseCase(
                    currentPassword = "password123",
                    newPassword = "  ",
                )

            assertStringResourceError(result, R.string.common_error_password_empty)
            coVerify(exactly = 0) { userRepository.changePassword(any(), any()) }
        }

    @Test
    fun `valid input trims passwords and calls repository`() =
        runTest {
            coEvery {
                userRepository.changePassword(
                    currentPassword = "old-password",
                    newPassword = "new-password",
                )
            } returns ApiResult.Success(message = UiText.DynamicString("Changed"))

            val result =
                changePasswordUseCase(
                    currentPassword = "  old-password  ",
                    newPassword = "  new-password  ",
                )

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) {
                userRepository.changePassword(
                    currentPassword = "old-password",
                    newPassword = "new-password",
                )
            }
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
