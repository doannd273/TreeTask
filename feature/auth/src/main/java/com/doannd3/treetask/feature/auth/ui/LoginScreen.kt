package com.doannd3.treetask.feature.auth.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Button
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.feature.auth.R
import com.doannd3.treetask.feature.auth.contract.AuthEffect
import com.doannd3.treetask.feature.auth.contract.AuthEvent
import com.doannd3.treetask.feature.auth.contract.AuthState
import com.doannd3.treetask.feature.auth.viewmodel.AuthViewModel

@Composable
fun LoginRoute(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    LoginScreen(
        state = state,
        onEmailChange = { viewModel.onEvent(AuthEvent.EmailChanged(it)) },
        onPasswordChange = { viewModel.onEvent(AuthEvent.PasswordChanged(it)) },
        onSubmitLogin = { viewModel.onEvent(AuthEvent.SubmitLogin) },
        onNavigateToRegister = onNavigateToRegister
    )

    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when (effect) {
                is AuthEffect.NavigateToHome -> {
                    onNavigateToHome()
                }

                is AuthEffect.ShowErrorMessage -> {

                }
            }
        }
    }
}

@Composable
fun LoginScreen(
    state: AuthState,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onSubmitLogin: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    val context = LocalContext.current

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .imePadding(),
            verticalArrangement = Arrangement.Center
        ) {
            // Email
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.email,
                onValueChange = { onEmailChange(it) },
                label = { Text(stringResource(R.string.auth_email_hint)) },
                isError = state.emailError != null,
                supportingText = { state.emailError?.let { Text(it.asString(context)) } })

            Spacer(modifier = Modifier.height(8.dp))

            // Password
            OutlinedTextField(
                modifier = Modifier.fillMaxWidth(),
                value = state.password,
                onValueChange = { onPasswordChange(it) },
                label = { Text(stringResource(R.string.auth_password_hint)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            // Login button
            Button(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = { onSubmitLogin },
                enabled = !state.isLoading
            ) {
                if (state.isLoading) CircularProgressIndicator()
                else Text(stringResource(R.string.auth_action_login))
            }

            // Register button
            TextButton(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                onClick = { onNavigateToRegister }
            ) {
                Text(stringResource(R.string.auth_prompt_no_account))
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {
    LoginScreen(
        state = AuthState(
            email = "demo@gmail.com",
            password = "123456"
        ),
        onEmailChange = {},
        onPasswordChange = {},
        onSubmitLogin = {},
        onNavigateToRegister = {}
    )
}