package com.doannd3.treetask.core.data.respository

import app.cash.turbine.test
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.datastore.TokenManager
import com.doannd3.treetask.core.datastore.UserPrefsManager
import com.doannd3.treetask.core.network.model.response.RegisterResponse
import com.doannd3.treetask.core.network.model.response.TokenResponse
import com.doannd3.treetask.core.network.service.AuthService
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class AuthRepositoryImplTest {
    private val authService: AuthService = mockk()
    private val tokenManager: TokenManager = mockk(relaxed = true)
    private val userPrefsManager: UserPrefsManager = mockk(relaxed = true)

    private lateinit var authRepository: AuthRepositoryImpl

    @Before
    fun setUp() {
        // Khởi tạo Repository bằng cách bơm các diễn viên đóng thế vào
        authRepository =
            AuthRepositoryImpl(
                authService = authService,
                tokenManager = tokenManager,
                userPrefsManager = userPrefsManager,
            )
    }

    @Test
    fun `isSessionExpired emits true when session expires`() =
        runTest {
            // GIVEN: Tạo ra ống nước kiểu SharedFlow (không cần nhét sẵn nước vào)
            val fakeEventFlow = MutableSharedFlow<Unit>()

            // Bắt gã diễn viên tokenManager trả về ống nước này
            every { tokenManager.sessionExpiredEvent } returns fakeEventFlow

            // WHEN & THEN: Dùng thư viện Turbine (test) để hứng dòng chảy
            authRepository.isSessionExpired.test {
                // HÀNH ĐỘNG: Nhỏ 1 giọt nước vào ống (Bắt buộc phải nhỏ SAU KHI ống Turbine đã được gắn vào)
                fakeEventFlow.emit(Unit)

                // Chờ hứng giọt nước (tín hiệu) đầu tiên rớt xuống
                val result = awaitItem()

                // Kết quả nhận được phải bị biến thành cờ TRUE
                assertThat(result).isTrue()

                // BẮT BUỘC: Vì SharedFlow chạy vĩnh viễn không bao giờ tự đóng, ta phải chủ động hủy
                // Tuyệt đối không dùng awaitComplete() ở đây vì nó sẽ chờ đến vô tận (gây lỗi 3s Timeout)
                cancelAndIgnoreRemainingEvents()
            }
        }

    @Test
    fun `login returns success and saves token`() =
        runTest {
            // GIVEN: Dàn cảnh kịch bản
            val fakeEmail = "test@treetask.com"
            val fakePassword = "password123"
            val fakeResponse =
                TokenResponse(
                    accessToken = "fake_access_token",
                    refreshToken = "fake_refresh_token",
                )
            // Dạy con bot API: Hễ có ai gọi login() với email và pass đó, thì trả về fakeResponse
            coEvery {
                authService.login(any())
            } returns ApiResult.Success(fakeResponse)

            // WHEN: Kịch bản bấm nút Login xảy ra
            val result = authRepository.login(fakeEmail, fakePassword)

            // THEN: Soi kết quả xem có đúng như mong đợi không?
            // 1. Kết quả phải là Success
            assertThat(result).isInstanceOf(ApiResult.Success::class.java)

            // 2. Chắc chắn rằng hàm saveToken đã được gọi đúng 1 lần với token chính xác
            coVerify(exactly = 1) {
                tokenManager.saveToken("fake_access_token", "fake_refresh_token")
            }
        }

    @Test
    fun `register returns success and saves token`() =
        runTest {
            // GIVEN: Dàn cảnh kịch bản
            val fakeFullName = "Nguyen123"
            val fakeEmail = "test@treetask.com"
            val fakePassword = "password123"
            val fakeResponse =
                RegisterResponse(
                    userId = "fake_user_id",
                    accessToken = "fake_access_token",
                    refreshToken = "fake_refresh_token",
                )
            // Dạy con bot API: Hễ có ai gọi register() với fullName, email và pass đó, thì trả về fakeResponse
            coEvery {
                authService.register(any())
            } returns ApiResult.Success(fakeResponse)

            // WHEN: Kịch bản bấm nút Register xảy ra
            val result = authRepository.register(fakeFullName, fakeEmail, fakePassword)

            // THEN: Soi kết quả xem có đúng như mong đợi không?
            // 1. Kết quả phải là Success
            assertThat(result).isInstanceOf(ApiResult.Success::class.java)

            // 2. Chắc chắn rằng hàm saveToken đã được gọi đúng 1 lần với token chính xác
            coVerify(exactly = 1) {
                tokenManager.saveToken("fake_access_token", "fake_refresh_token")
            }
        }

    @Test
    fun `forgotPassword returns success`() =
        runTest {
            // GIVE
            val fakeEmail = "test@treetask.com"

            // Dạy con bot API: Hễ có ai gọi forgotPassword() với email đó, thì trả về Unit
            coEvery {
                authService.forgotPassword(any())
            } returns ApiResult.Success(Unit)

            // WHEN: Kịch bản bấm nút forgotPassword xảy ra
            val result = authRepository.forgotPassword(fakeEmail)

            // THEN: Soi kết quả xem có đúng như mong đợi không?
            // 1. Kết quả phải là Success
            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
        }

    @Test
    fun `logout clears token and user profile`() =
        runTest {
            // GIVEN: Không cần dàn cảnh gì cả vì gã diễn viên mockk(relaxed = true) đã tự biết gật đầu rồi.

            // WHEN: Kịch bản người dùng bấm nút Đăng Xuất xảy ra
            authRepository.logout()

            // THEN: Cảnh sát (coVerify) nhảy vào kiểm tra hiện trường

            // 1. Chắc chắn hàm clearToken đã được gọi đúng 1 lần
            coVerify(exactly = 1) {
                tokenManager.clearToken()
            }
            // 2. Chắc chắn hàm clearUserProfile đã được gọi đúng 1 lần
            coVerify(exactly = 1) {
                userPrefsManager.clearUserProfile()
            }
        }
}
