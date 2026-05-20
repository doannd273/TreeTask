package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.error.AppErrorCode
import com.doannd3.treetask.core.common.error.MissingResponseDataException
import com.doannd3.treetask.core.datastore.user.UserStorage
import com.doannd3.treetask.core.network.model.response.UserResponse
import com.doannd3.treetask.core.network.service.UserService
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UserRepositoryImplTest {
    private val userService: UserService = mockk()
    private val userStorage: UserStorage = mockk(relaxed = true)

    private lateinit var userRepository: UserRepositoryImpl

    @Before
    fun setUp() {
        userRepository =
            UserRepositoryImpl(
                userService = userService,
                userStorage = userStorage,
            )
    }

    @Test
    fun `getProfile returns missing response data and does not save profile when payload is missing`() =
        runTest {
            coEvery {
                userService.getProfile()
            } returns ApiResult.Success<UserResponse>(data = null)

            val result = userRepository.getProfile()

            assertMissingResponseDataError(result)
            coVerify(exactly = 0) {
                userStorage.saveUserProfile(any())
            }
        }

    @Test
    fun `getProfile returns missing response data and does not save profile when user payload is invalid`() =
        runTest {
            val invalidUser =
                UserResponse(
                    id = "user_123",
                    fullName = "Doan ND",
                    email = "",
                )

            coEvery {
                userService.getProfile()
            } returns ApiResult.Success(data = invalidUser)

            val result = userRepository.getProfile()

            assertMissingResponseDataError(result)
            coVerify(exactly = 0) {
                userStorage.saveUserProfile(any())
            }
        }

    private fun assertMissingResponseDataError(result: ApiResult<*>) {
        assertThat(result).isInstanceOf(ApiResult.Error::class.java)
        val error = result as ApiResult.Error
        assertThat(error.appErrorCode).isEqualTo(AppErrorCode.MISSING_RESPONSE_DATA)
        assertThat(error.exception).isInstanceOf(MissingResponseDataException::class.java)
    }
}
