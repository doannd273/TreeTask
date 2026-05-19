package com.doannd3.treetask.feature.auth.ui.forgotpassword

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.auth.ForgotPasswordUseCase
import com.doannd3.treetask.core.domain.usecase.auth.ResetPasswordUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject
import com.doannd3.treetask.core.common.R as CommonR
import com.doannd3.treetask.feature.auth.R as AuthR

@HiltViewModel
class ForgotPasswordViewModel
@Inject
constructor(
    private val forgotPasswordUseCase: ForgotPasswordUseCase,
    private val resetPasswordUseCase: ResetPasswordUseCase,
) : BaseViewModel(),
    MviViewModel<ForgotPasswordState, ForgotPasswordEvent, ForgotPasswordEffect> {
    override fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }

    private val _uiState = MutableStateFlow(ForgotPasswordState())
    override val uiState: StateFlow<ForgotPasswordState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ForgotPasswordEffect>()
    override val effect: SharedFlow<ForgotPasswordEffect> = _effect.asSharedFlow()

    override fun onEvent(event: ForgotPasswordEvent) {
        when (event) {
            is ForgotPasswordEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }

            is ForgotPasswordEvent.SubmitEmail -> {
                submitEmail()
            }

            is ForgotPasswordEvent.SubmitResetPassword -> {
                submitResetPassword()
            }

            ForgotPasswordEvent.BackToEmailInput -> {
                _uiState.update {
                    it.copy(
                        step = ForgotPasswordStep.EmailInput,
                        otp = "",
                        newPassword = "",
                        passwordVisible = false,
                    )
                }
            }

            is ForgotPasswordEvent.NewPasswordChanged -> {
                _uiState.update { it.copy(newPassword = event.newPassword) }
            }

            is ForgotPasswordEvent.OtpChanged -> {
                _uiState.update { it.copy(otp = event.otp) }
            }

            is ForgotPasswordEvent.PasswordVisibleChanged -> {
                _uiState.update { it.copy(passwordVisible = event.passwordVisible) }
            }

            ForgotPasswordEvent.ResendOtp -> {
                _uiState.update { it.copy(otp = "") }
                submitEmail()
            }

            ForgotPasswordEvent.ResetPasswordAcknowledged -> {
                viewModelScope.launch {
                    _effect.emit(ForgotPasswordEffect.NavigateToLogin)
                }
            }
        }
    }

    private fun submitResetPassword() {
        val state = uiState.value

        if (state.isLoading) {
            return
        }

        executeSafe {
            _uiState.update { it.copy(isLoading = true) }

            val result =
                resetPasswordUseCase(
                    email = state.email,
                    otp = state.otp,
                    newPassword = state.newPassword,
                )

            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is ApiResult.Success -> {
                    val message =
                        result.message
                            ?: UiText.StringResource(AuthR.string.auth_reset_password_success)
                    _effect.emit(ForgotPasswordEffect.ResetPasswordSuccess(message))
                }

                is ApiResult.Error -> {
                    val message =
                        result.toDisplayMessage(
                            UiText.StringResource(CommonR.string.common_error_unknown),
                        )
                    _effect.emit(ForgotPasswordEffect.ShowErrorMessage(message))
                }
            }
        }
    }

    private fun submitEmail() {
        val state = uiState.value

        if (state.isLoading) {
            return
        }

        executeSafe {
            _uiState.update { it.copy(isLoading = true) }

            val result = forgotPasswordUseCase(email = state.email)

            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is ApiResult.Success -> {
                    _uiState.update {
                        it.copy(
                            step = ForgotPasswordStep.ResetInput,
                        )
                    }

                    val message =
                        result.message
                            ?: UiText.StringResource(AuthR.string.auth_forgot_password_success)
                    _effect.emit(ForgotPasswordEffect.SendEmailSuccess(message))
                }

                is ApiResult.Error -> {
                    val message =
                        result.toDisplayMessage(
                            UiText.StringResource(CommonR.string.common_error_unknown),
                        )
                    _effect.emit(ForgotPasswordEffect.ShowErrorMessage(message))
                }
            }
        }
    }
}
