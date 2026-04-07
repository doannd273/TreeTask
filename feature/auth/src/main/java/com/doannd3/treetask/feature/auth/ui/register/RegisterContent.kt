package com.doannd3.treetask.feature.auth.ui.register

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
import com.doannd3.treetask.feature.auth.ui.login.LoginState
import com.doannd3.treetask.feature.auth.ui.login.EmailInput
import com.doannd3.treetask.feature.auth.ui.login.PasswordInput

@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    state: RegisterState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmitRegister: () -> Unit,
    onRegisterBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        RegisterHeader(onRegisterBack = onRegisterBack)

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            FullNameInput(
                fullName = state.fullName,
                onFullNameChange = onFullNameChange
            )

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

            Spacer(modifier = Modifier.height(16.dp))

            RegisterButton(
                onSubmitRegister = onSubmitRegister
            )
        }
    }
}