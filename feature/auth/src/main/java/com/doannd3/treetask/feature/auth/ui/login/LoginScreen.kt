package com.doannd3.treetask.feature.auth.ui.login

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
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.AppLoadingDialog
import com.doannd3.treetask.core.designsystem.component.CommonButton
import com.doannd3.treetask.core.designsystem.component.EmailInput
import com.doannd3.treetask.core.designsystem.component.PasswordInput
import com.doannd3.treetask.core.designsystem.component.message.AppDialogType
import com.doannd3.treetask.core.designsystem.component.message.AppMessage
import com.doannd3.treetask.core.designsystem.component.message.AppMessageDialogHost
import com.doannd3.treetask.core.designsystem.component.message.AppMessageId
import com.doannd3.treetask.core.designsystem.component.message.rememberAppMessageHostState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.designsystem.util.rememberDebouncedClick
import com.doannd3.treetask.feature.auth.R

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val messageHostState = rememberAppMessageHostState()

    LoginScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToForgotPassword = onNavigateToForgotPassword,
    )

    AppMessageDialogHost(
        state = messageHostState,
        onAcknowledged = {},
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is LoginEffect.NavigateToHome -> {
                        onNavigateToHome()
                    }

                    is LoginEffect.ShowErrorMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = LoginMessageIds.Error,
                                message = effect.message.asString(context),
                                type = AppDialogType.Error,
                            ),
                        )
                    }
                }
            }
        }
    }

    LaunchedEffect(viewModel.baseErrorEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.baseErrorEffect.collect { message ->
                messageHostState.enqueue(
                    AppMessage(
                        id = LoginMessageIds.Error,
                        message = message.asString(context),
                        type = AppDialogType.Error,
                    ),
                )
            }
        }
    }
}

@Composable
internal fun LoginScreen(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingValues ->
        LoginContent(
            modifier = Modifier.padding(paddingValues = paddingValues),
            state = state,
            onEvent = onEvent,
            onNavigateToRegister = onNavigateToRegister,
            onNavigateToForgotPassword = onNavigateToForgotPassword,
        )
    }

    AppLoadingDialog(isLoading = state.isLoading)
}

@AppPreviewLightDark
@Composable
private fun LoginScreenPreview() {
    TreeTaskTheme {
        LoginScreen(
            state =
            LoginState(
                email = "demo@gmail.com",
                password = "123456",
            ),
            onEvent = {},
            onNavigateToRegister = {},
            onNavigateToForgotPassword = {},
        )
    }
}

@Composable
internal fun LoginContent(
    state: LoginState,
    onEvent: (LoginEvent) -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val onSubmitLoginDebounced =
        rememberDebouncedClick {
            onEvent(LoginEvent.SubmitLogin)
        }

    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .padding(16.dp)
            .imePadding(),
        verticalArrangement = Arrangement.Center,
    ) {
        TreeTaskAppName()

        Spacer(modifier = Modifier.height(16.dp))

        EmailInput(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.auth_email_hint),
            email = state.email,
            onEmailChange = { onEvent(LoginEvent.EmailChanged(it)) },
            imeAction = ImeAction.Next,
            onImeNext = {
                passwordFocusRequester.requestFocus()
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        PasswordInput(
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
            label = stringResource(R.string.auth_password_hint),
            password = state.password,
            passwordVisible = state.passwordVisible,
            onPasswordChange = { onEvent(LoginEvent.PasswordChanged(it)) },
            onPasswordVisibleChange = { onEvent(LoginEvent.PasswordVisibleChanged(it)) },
            onImeDone = {
                focusManager.clearFocus()
                onSubmitLoginDebounced()
            },
        )

        Spacer(modifier = Modifier.height(8.dp))

        ForgotPasswordTextButton(onNavigateToForgotPassword = onNavigateToForgotPassword)

        Spacer(modifier = Modifier.height(16.dp))

        CommonButton(
            buttonText = stringResource(R.string.auth_action_login),
            isEnable = !state.isLoading,
            onSubmit = onSubmitLoginDebounced,
        )

        Spacer(modifier = Modifier.height(16.dp))

        RegisterTextButton(
            onNavigateToRegister = onNavigateToRegister,
        )
    }
}

private object LoginMessageIds {
    val Error = AppMessageId("login-error")
}
