package com.doannd3.treetask.feature.auth.ui.login

import com.doannd3.treetask.core.common.UiText

data class LoginState(
    val email: String = "",
    val password: String = "",
    val isLoading: Boolean = false
)

sealed class LoginEvent {
    data class EmailChanged(val email: String) : LoginEvent()
    data class PasswordChanged(val password: String) : LoginEvent()
    data object SubmitLogin : LoginEvent()
}

sealed class LoginEffect {
    data class ShowErrorMessage(val message: UiText) : LoginEffect()
    data object NavigateToHome : LoginEffect()
}