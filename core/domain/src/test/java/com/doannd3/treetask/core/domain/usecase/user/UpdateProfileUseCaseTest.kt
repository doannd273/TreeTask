package com.doannd3.treetask.core.domain.usecase.user

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.doannd3.treetask.core.model.user.User
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UpdateProfileUseCaseTest {
    private lateinit var updateProfileUseCase: UpdateProfileUseCase
    private val userRepository: UserRepository = mockk()

    @Before
    fun setUp() {
        updateProfileUseCase = UpdateProfileUseCase(userRepository)
    }

    @Test
    fun `blank full name returns full name empty error`() =
        runTest {
            val result =
                updateProfileUseCase(
                    fullName = "  ",
                    phone = "0123456789",
                    avatar = "avatar",
                )

            assertStringResourceError(result, R.string.common_error_fullName_empty)
            coVerify(exactly = 0) { userRepository.updateProfile(any(), any(), any()) }
        }

    @Test
    fun `valid input trims fields and calls repository`() =
        runTest {
            val user =
                User(
                    id = "user-id",
                    email = "user@treetask.com",
                    fullName = "Doan ND",
                    avatar = "avatar",
                    phone = "0123456789",
                )
            coEvery {
                userRepository.updateProfile(
                    fullName = "Doan ND",
                    phone = "0123456789",
                    avatar = "avatar",
                )
            } returns ApiResult.Success(data = user)

            val result =
                updateProfileUseCase(
                    fullName = "  Doan ND  ",
                    phone = "  0123456789  ",
                    avatar = "  avatar  ",
                )

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) {
                userRepository.updateProfile(
                    fullName = "Doan ND",
                    phone = "0123456789",
                    avatar = "avatar",
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
