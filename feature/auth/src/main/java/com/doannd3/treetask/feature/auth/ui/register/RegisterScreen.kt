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
import androidx.compose.runtime.rememberUpdatedState
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
    val currentContext by rememberUpdatedState(context)
    val currentOnNavigateToHome by rememberUpdatedState(onNavigateToHome)

    RegisterScreen(
        state = state,
        onFullNameChange = { viewModel.onEvent(RegisterEvent.FullNameChanged(it)) },
        onEmailChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
        onPasswordChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
        onConfirmPasswordChange = { viewModel.onEvent(RegisterEvent.ConfirmPasswordChanged(it)) },
        onPasswordVisibleChange = { viewModel.onEvent(RegisterEvent.PasswordVisibleChanged(it)) },
        onConfirmPasswordVisibleChange = {
            viewModel.onEvent(RegisterEvent.ConfirmPasswordVisibleChanged(it))
        },
        onSubmitRegister = { viewModel.onEvent(RegisterEvent.SubmitRegister) },
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
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Success,
                            ),
                        )
                    }

                    is RegisterEffect.ShowErrorMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = RegisterMessageIds.Error,
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Error,
                            ),
                        )
                    }

                    is RegisterEffect.NavigateToHome -> {
                        currentOnNavigateToHome()
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
                        message = message.asString(currentContext),
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
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    onSubmitRegister: () -> Unit,
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
            onFullNameChange = onFullNameChange,
            onEmailChange = onEmailChange,
            onPasswordChange = onPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onPasswordVisibleChange = onPasswordVisibleChange,
            onConfirmPasswordVisibleChange = onConfirmPasswordVisibleChange,
            onSubmitRegister = onSubmitRegister,
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
            onFullNameChange = {},
            onEmailChange = {},
            onPasswordChange = {},
            onConfirmPasswordChange = {},
            onPasswordVisibleChange = {},
            onConfirmPasswordVisibleChange = {},
            onSubmitRegister = {},
            onRegisterBack = {},
        )
    }
}

@Composable
internal fun RegisterContent(
    state: RegisterState,
    onFullNameChange: (String) -> Unit,
    onEmailChange: (String) -> Unit,
    onPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    onSubmitRegister: () -> Unit,
    onRegisterBack: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val onSubmitRegisterDebounced =
        rememberDebouncedClick {
            onSubmitRegister()
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
                onFullNameChange = onFullNameChange,
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
                onEmailChange = onEmailChange,
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
                onPasswordChange = onPasswordChange,
                onPasswordVisibleChange = onPasswordVisibleChange,
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
                onPasswordChange = onConfirmPasswordChange,
                onPasswordVisibleChange = onConfirmPasswordVisibleChange,
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
