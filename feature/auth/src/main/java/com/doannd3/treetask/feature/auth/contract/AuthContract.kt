package com.doannd3.treetask.feature.auth.contract

import com.doannd3.treetask.core.common.UiText

data class AuthState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false,
    val emailError: UiText? = null,
    val passwordError: UiText? = null
)

sealed class AuthEvent {
    data class EmailChanged(val email: String) : AuthEvent()
    data class PasswordChanged(val password: String) : AuthEvent()
    data object SubmitLogin : AuthEvent()
    data object SubmitRegister : AuthEvent()
}

sealed class AuthEffect {
    data object NavigateToHome : AuthEffect()
    data class ShowErrorMessage(val message: UiText) : AuthEffect()
}