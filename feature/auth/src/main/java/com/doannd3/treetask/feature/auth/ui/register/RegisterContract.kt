package com.doannd3.treetask.feature.auth.ui.register

import com.doannd3.treetask.core.common.UiText

data class RegisterState(
    val fullName: String = "",
    val email: String = "",
    val password: String = "",
    val confirmPassword: String = "",
    val passwordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
)

sealed class RegisterEvent {
    data class FullNameChanged(
        val fullName: String,
    ) : RegisterEvent()

    data class EmailChanged(
        val email: String,
    ) : RegisterEvent()

    data class PasswordChanged(
        val password: String,
    ) : RegisterEvent()

    data class ConfirmPasswordChanged(
        val confirmPassword: String,
    ) : RegisterEvent()

    data class PasswordVisibleChanged(
        val passwordVisible: Boolean,
    ) : RegisterEvent()

    data class ConfirmPasswordVisibleChanged(
        val confirmPasswordVisible: Boolean,
    ) : RegisterEvent()

    data object SubmitRegister : RegisterEvent()

    data object SuccessAcknowledged : RegisterEvent()
}

sealed class RegisterEffect {
    data class ShowSuccessMessage(
        val message: UiText,
    ) : RegisterEffect()

    data class ShowErrorMessage(
        val message: UiText,
    ) : RegisterEffect()

    data object NavigateToHome : RegisterEffect()
}
