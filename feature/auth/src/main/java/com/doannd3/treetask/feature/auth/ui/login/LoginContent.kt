package com.doannd3.treetask.feature.auth.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
internal fun LoginContent(
    modifier: Modifier = Modifier,
    state: LoginState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmitLogin: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center
    ) {
        TreeTaskAppName()

        Spacer(modifier = Modifier.height(16.dp))

        EmailInput(
            email = state.email,
            onEmailChange = onEmailChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordInput(
            password = state.password,
            onPasswordChange = onPasswordChange
        )

        Spacer(modifier = Modifier.height(8.dp))

        ForgotPasswordTextButton(onNavigateToForgotPassword = onNavigateToForgotPassword)

        Spacer(modifier = Modifier.height(16.dp))

        LoginButton(
            onSubmitLogin = onSubmitLogin
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegisterTextButton(
            onNavigateToRegister = onNavigateToRegister
        )
    }
}