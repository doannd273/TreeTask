package com.doannd3.treetask.feature.auth.contract

import com.doannd3.treetask.core.common.UiText

data class ForgotPasswordState(
    val email: String = "",
    val isLoading: Boolean = false,
)

sealed class ForgotPasswordEvent {
    data object SubmitForgotPassword : ForgotPasswordEvent()
    data class EmailChanged(val email: String) : ForgotPasswordEvent()
}

sealed class ForgotPasswordEffect {
    data object NavigateToLogin : ForgotPasswordEffect()
    data class ShowErrorMessage(val message: UiText) : ForgotPasswordEffect()
}