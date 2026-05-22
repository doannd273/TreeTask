package com.doannd3.treetask.feature.profile.ui.changepassword

import com.doannd3.treetask.core.common.UiText

data class ChangePasswordState(
    val currentPassword: String = "",
    val newPassword: String = "",
    val confirmPassword: String = "",
    val currentPasswordVisible: Boolean = false,
    val newPasswordVisible: Boolean = false,
    val confirmPasswordVisible: Boolean = false,
    val isLoading: Boolean = false,
)

sealed class ChangePasswordEvent {
    data object SubmitChangePassword : ChangePasswordEvent()

    data class CurrentPasswordChanged(
        val currentPassword: String,
    ) : ChangePasswordEvent()

    data class NewPasswordChanged(
        val newPassword: String,
    ) : ChangePasswordEvent()

    data class ConfirmPasswordChanged(
        val confirmPassword: String,
    ) : ChangePasswordEvent()

    data class CurrentPasswordVisibleChanged(
        val visible: Boolean,
    ) : ChangePasswordEvent()

    data class NewPasswordVisibleChanged(
        val visible: Boolean,
    ) : ChangePasswordEvent()

    data class ConfirmPasswordVisibleChanged(
        val visible: Boolean,
    ) : ChangePasswordEvent()

    data object SuccessAcknowledged : ChangePasswordEvent()
}

sealed class ChangePasswordEffect {
    data class ShowErrorMessage(
        val message: UiText,
    ) : ChangePasswordEffect()

    data class ShowSuccessMessage(
        val message: UiText,
    ) : ChangePasswordEffect()

    data object NavigateBack : ChangePasswordEffect()
}
