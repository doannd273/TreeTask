package com.doannd3.treetask.feature.auth.ui.register

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.doannd3.treetask.feature.auth.ui.login.LoginState

@Composable
fun RegisterScreen(
    state: RegisterState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmitRegister: () -> Unit,
    onRegisterBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        RegisterContent(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            state = state,
            onFullNameChange = onFullNameChange,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onSubmitRegister = onSubmitRegister,
            onRegisterBack = onRegisterBack,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun RegisterScreenPreview() {
    RegisterScreen(
        state = RegisterState(
            fullName = "Nguyễn Demo",
            email = "demo@gmail.com",
            password = "123456"
        ),
        onFullNameChange = {},
        onEmailChange = {},
        onPasswordChange = {},
        onSubmitRegister = {},
        onRegisterBack = {}
    )
}