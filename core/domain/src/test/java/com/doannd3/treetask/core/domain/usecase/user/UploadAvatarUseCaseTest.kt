package com.doannd3.treetask.core.domain.usecase.user

import android.net.Uri
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.every
import io.mockk.mockk
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Test

class UploadAvatarUseCaseTest {
    private lateinit var uploadAvatarUseCase: UploadAvatarUseCase
    private val userRepository: UserRepository = mockk()

    @Before
    fun setUp() {
        uploadAvatarUseCase = UploadAvatarUseCase(userRepository)
    }

    @Test
    fun `blank avatar uri returns avatar empty error`() =
        runTest {
            val uri: Uri = mockk()
            every { uri.toString() } returns ""

            val result = uploadAvatarUseCase(uri = uri)

            assertStringResourceError(result, R.string.common_error_avatar_empty)
            coVerify(exactly = 0) { userRepository.uploadFile(any()) }
        }

    @Test
    fun `valid avatar uri calls repository`() =
        runTest {
            val uri: Uri = mockk()
            every { uri.toString() } returns "content://avatar"
            coEvery { userRepository.uploadFile(uri = uri) } returns ApiResult.Success(data = "avatar")

            val result = uploadAvatarUseCase(uri = uri)

            assertThat(result).isInstanceOf(ApiResult.Success::class.java)
            coVerify(exactly = 1) { userRepository.uploadFile(uri = uri) }
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
