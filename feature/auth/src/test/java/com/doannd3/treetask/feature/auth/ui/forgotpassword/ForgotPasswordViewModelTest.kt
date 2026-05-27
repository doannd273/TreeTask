package com.doannd3.treetask.feature.auth.ui.forgotpassword

import androidx.lifecycle.viewmodel.compose.viewModel
import app.cash.turbine.test
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.usecase.auth.ForgotPasswordUseCase
import com.doannd3.treetask.core.domain.usecase.auth.ResetPasswordUseCase
import com.doannd3.treetask.core.testing.util.MainDispatcherRule
import com.google.common.truth.Truth.assertThat
import io.mockk.coEvery
import io.mockk.coVerify
import io.mockk.mockk
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.advanceUntilIdle
import kotlinx.coroutines.test.runCurrent
import kotlinx.coroutines.test.runTest
import org.junit.Before
import org.junit.Rule
import org.junit.Test

@OptIn(ExperimentalCoroutinesApi::class)
class ForgotPasswordViewModelTest {

    @get:Rule
    val mainDispatcherRule = MainDispatcherRule()

    private val forgotPasswordUseCase: ForgotPasswordUseCase = mockk()
    private val resetPasswordUseCase: ResetPasswordUseCase = mockk()

    private lateinit var viewModel: ForgotPasswordViewModel

    @Before
    fun setUp() {
        viewModel =
            ForgotPasswordViewModel(
                forgotPasswordUseCase = forgotPasswordUseCase,
                resetPasswordUseCase = resetPasswordUseCase,
            )
    }

    @Test
    fun `submit email success moves to reset input and emits success effect`() = runTest {
        //  GIVEN
        val email = "doan@test.com"
        val message = UiText.DynamicString("Otp sent")

        coEvery { forgotPasswordUseCase(email = email) } returns ApiResult.Success(message = message)

        viewModel.onEvent(ForgotPasswordEvent.EmailChanged(email = email))

        // WHEN
        viewModel.effect.test {
            viewModel.onEvent(ForgotPasswordEvent.SubmitEmail)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(ForgotPasswordEffect.SendEmailSuccess(message = message))
            assertThat(viewModel.uiState.value.step).isEqualTo(ForgotPasswordStep.ResetInput)
            assertThat(viewModel.uiState.value.isLoading).isFalse()

            cancelAndIgnoreRemainingEvents()
        }

        // THEN
        coVerify(exactly = 1) { forgotPasswordUseCase(email = email) }
    }

    @Test
    fun `resend otp clears otp and calls forgot password flow`() = runTest {
        val email = "doan@test.com"

        coEvery { forgotPasswordUseCase(email) } returns ApiResult.Success(
            message = UiText.DynamicString("OTP resent"),
        )

        viewModel.onEvent(ForgotPasswordEvent.EmailChanged(email))
        viewModel.onEvent(ForgotPasswordEvent.OtpChanged("123456"))

        viewModel.effect.test {
            viewModel.onEvent(ForgotPasswordEvent.ResendOtp)
            advanceUntilIdle()

            assertThat(awaitItem()).isInstanceOf(ForgotPasswordEffect.SendEmailSuccess::class.java)
            assertThat(viewModel.uiState.value.otp).isEmpty()

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { forgotPasswordUseCase(email) }
        coVerify(exactly = 0) { resetPasswordUseCase(any(), any(), any()) }
    }

    @Test
    fun `submit reset password success emits reset password success effect`() = runTest {
        val email = "doan@test.com"
        val otp = "123456"
        val password = "password123"
        val message = UiText.DynamicString("Password reset")

        coEvery { resetPasswordUseCase(email, otp, password) } returns ApiResult.Success(message = message)

        viewModel.onEvent(ForgotPasswordEvent.EmailChanged(email))
        viewModel.onEvent(ForgotPasswordEvent.OtpChanged(otp))
        viewModel.onEvent(ForgotPasswordEvent.NewPasswordChanged(password))
        viewModel.onEvent(ForgotPasswordEvent.ConfirmPasswordChanged(password))

        viewModel.effect.test {
            viewModel.onEvent(ForgotPasswordEvent.SubmitResetPassword)
            advanceUntilIdle()

            assertThat(awaitItem()).isEqualTo(ForgotPasswordEffect.ResetPasswordSuccess(message))
            assertThat(viewModel.uiState.value.isLoading).isFalse()

            cancelAndIgnoreRemainingEvents()
        }

        coVerify(exactly = 1) { resetPasswordUseCase(email, otp, password) }
    }

    @Test
    fun `back to email input clears reset fields`() {
        viewModel.onEvent(ForgotPasswordEvent.OtpChanged("123456"))
        viewModel.onEvent(ForgotPasswordEvent.NewPasswordChanged("password123"))
        viewModel.onEvent(ForgotPasswordEvent.PasswordVisibleChanged(true))

        viewModel.onEvent(ForgotPasswordEvent.BackToEmailInput)

        val state = viewModel.uiState.value
        assertThat(state.step).isEqualTo(ForgotPasswordStep.EmailInput)
        assertThat(state.otp).isEmpty()
        assertThat(state.newPassword).isEmpty()
        assertThat(state.passwordVisible).isFalse()
    }

    @Test
    fun `submit email while loading does not call forgot password twice`() = runTest {
        val email = "doan@test.com"
        val pendingResult = CompletableDeferred<ApiResult<String>>()

        coEvery { forgotPasswordUseCase(email) } coAnswers {
            pendingResult.await()
        }

        viewModel.onEvent(ForgotPasswordEvent.EmailChanged(email))

        viewModel.effect.test {
            viewModel.onEvent(ForgotPasswordEvent.SubmitEmail)
            runCurrent()

            assertThat(viewModel.uiState.value.isLoading).isTrue()

            viewModel.onEvent(ForgotPasswordEvent.SubmitEmail)
            runCurrent()

            coVerify(exactly = 1) { forgotPasswordUseCase(email) }

            pendingResult.complete(ApiResult.Success(message = UiText.DynamicString("OTP sent")))
            advanceUntilIdle()

            assertThat(awaitItem()).isInstanceOf(ForgotPasswordEffect.SendEmailSuccess::class.java)
            cancelAndIgnoreRemainingEvents()
        }
    }
}
