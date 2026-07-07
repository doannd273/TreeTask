package com.doannd3.treetask.feature.auth.ui.register

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
import com.doannd3.treetask.core.designsystem.component.CommonHeader
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
fun RegisterRoute(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onRegisterBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val messageHostState = rememberAppMessageHostState()

    RegisterScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onRegisterBack = onRegisterBack,
    )

    AppMessageDialogHost(
        state = messageHostState,
        onAcknowledged = { message ->
            if (message.id == RegisterMessageIds.Success) {
                viewModel.onEvent(RegisterEvent.SuccessAcknowledged)
            }
        },
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is RegisterEffect.ShowSuccessMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = RegisterMessageIds.Success,
                                message = effect.message.asString(context),
                                type = AppDialogType.Success,
                            ),
                        )
                    }

                    is RegisterEffect.ShowErrorMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = RegisterMessageIds.Error,
                                message = effect.message.asString(context),
                                type = AppDialogType.Error,
                            ),
                        )
                    }

                    is RegisterEffect.NavigateToHome -> {
                        onNavigateToHome()
                    }
                }
            }
        }
    }

    // Lỗi crash/unexpected từ BaseViewModel (CoroutineExceptionHandler)
    LaunchedEffect(viewModel.baseErrorEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.baseErrorEffect.collect { message ->
                messageHostState.enqueue(
                    AppMessage(
                        id = RegisterMessageIds.Error,
                        message = message.asString(context),
                        type = AppDialogType.Error,
                    ),
                )
            }
        }
    }
}

@Composable
internal fun RegisterScreen(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
    onRegisterBack: () -> Unit,
) {
    Scaffold(
        contentWindowInsets =
        WindowInsets.safeDrawing,
        topBar = {
            CommonHeader(
                title = stringResource(R.string.auth_register),
                onNavigateBack = onRegisterBack,
            )
        },
    ) { paddingValues ->
        RegisterContent(
            state = state,
            onEvent = onEvent,
            onRegisterBack = onRegisterBack,
            modifier =
            Modifier.padding(
                paddingValues = paddingValues,
            ),
        )
    }

    AppLoadingDialog(isLoading = state.isLoading)
}

@AppPreviewLightDark
@Composable
private fun RegisterScreenPreview() {
    TreeTaskTheme {
        RegisterScreen(
            state =
            RegisterState(
                fullName = "Nguyen Demo",
                email = "demo@gmail.com",
                password = "123456",
                confirmPassword = "12321",
            ),
            onEvent = {},
            onRegisterBack = {},
        )
    }
}

@Composable
internal fun RegisterContent(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
    onRegisterBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val onSubmitRegisterDebounced =
        rememberDebouncedClick {
            onEvent(RegisterEvent.SubmitRegister)
        }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            FullNameInput(
                fullName = state.fullName,
                onFullNameChange = { onEvent(RegisterEvent.FullNameChanged(it)) },
                onImeNext = {
                    emailFocusRequester.requestFocus()
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            EmailInput(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocusRequester),
                label = stringResource(R.string.auth_email_hint),
                email = state.email,
                onEmailChange = { onEvent(RegisterEvent.EmailChanged(it)) },
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
                onPasswordChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
                onPasswordVisibleChange = { onEvent(RegisterEvent.PasswordVisibleChanged(it)) },
                imeAction = ImeAction.Next,
                onImeNext = {
                    confirmPasswordFocusRequester.requestFocus()
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(confirmPasswordFocusRequester),
                label = stringResource(R.string.auth_confirm_password_hint),
                password = state.confirmPassword,
                passwordVisible = state.confirmPasswordVisible,
                onPasswordChange = { onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
                onPasswordVisibleChange = { onEvent(RegisterEvent.ConfirmPasswordVisibleChanged(it)) },
                imeAction = ImeAction.Done,
                onImeDone = {
                    focusManager.clearFocus()
                    onSubmitRegisterDebounced()
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            CommonButton(
                buttonText = stringResource(R.string.auth_register),
                isEnable = !state.isLoading,
                onSubmit = onSubmitRegisterDebounced,
            )

            Spacer(modifier = Modifier.height(16.dp))

            AlreadyHaveAccountTextButton(onRegisterBack = onRegisterBack)
        }
    }
}

private object RegisterMessageIds {
    val Error = AppMessageId("register-error")
    val Success = AppMessageId("register-success")
}
