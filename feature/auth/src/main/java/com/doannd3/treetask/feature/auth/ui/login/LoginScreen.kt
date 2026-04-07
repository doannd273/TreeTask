package com.doannd3.treetask.feature.auth.ui.login

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview

@Composable
fun LoginScreen(
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmitLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        LoginContent(
            modifier = Modifier.padding(paddingValues = paddingValues),
            state = state,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onSubmitLogin = onSubmitLogin,
            onNavigateToRegister = onNavigateToRegister,
            onNavigateToForgotPassword = onNavigateToForgotPassword,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {
    LoginScreen(
        state = LoginState(
            email = "demo@gmail.com",
            password = "123456"
        ),
        onEmailChange = {},
        onPasswordChange = {},
        onSubmitLogin = {},
        onNavigateToRegister = {},
        onNavigateToForgotPassword = {},
    )
}