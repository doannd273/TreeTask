package com.doannd3.treetask.feature.auth.ui.forgotpassword

import com.doannd3.treetask.core.common.UiText

enum class ForgotPasswordStep {
    EmailInput,
    ResetInput,
}

data class ForgotPasswordState(
    val step: ForgotPasswordStep = ForgotPasswordStep.EmailInput,
    val email: String = "",
    val otp: String = "",
    val newPassword: String = "",
    val isLoading: Boolean = false,
    val passwordVisible: Boolean = false,
)

sealed class ForgotPasswordEvent {
    data object SubmitEmail : ForgotPasswordEvent()

    data object SubmitResetPassword : ForgotPasswordEvent()

    data object BackToEmailInput : ForgotPasswordEvent()

    data object ResendOtp : ForgotPasswordEvent()

    data object ResetPasswordAcknowledged : ForgotPasswordEvent()

    data class EmailChanged(
        val email: String,
    ) : ForgotPasswordEvent()

    data class OtpChanged(
        val otp: String,
    ) : ForgotPasswordEvent()

    data class NewPasswordChanged(
        val newPassword: String,
    ) : ForgotPasswordEvent()

    data class PasswordVisibleChanged(
        val passwordVisible: Boolean,
    ) : ForgotPasswordEvent()
}

sealed class ForgotPasswordEffect {
    data class SendEmailSuccess(
        val message: UiText,
    ) : ForgotPasswordEffect()

    data class ShowErrorMessage(
        val message: UiText,
    ) : ForgotPasswordEffect()

    data class ResetPasswordSuccess(
        val message: UiText,
    ) : ForgotPasswordEffect()

    data object NavigateToLogin : ForgotPasswordEffect()
}
