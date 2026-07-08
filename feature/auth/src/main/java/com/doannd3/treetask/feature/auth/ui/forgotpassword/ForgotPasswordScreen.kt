package com.doannd3.treetask.feature.auth.ui.forgotpassword

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.AppLoadingDialog
import com.doannd3.treetask.core.designsystem.component.CommonHeader
import com.doannd3.treetask.core.designsystem.component.message.AppDialogType
import com.doannd3.treetask.core.designsystem.component.message.AppMessage
import com.doannd3.treetask.core.designsystem.component.message.AppMessageDialogHost
import com.doannd3.treetask.core.designsystem.component.message.AppMessageId
import com.doannd3.treetask.core.designsystem.component.message.rememberAppMessageHostState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.feature.auth.R

@Composable
fun ForgotPasswordRoute(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onForgotPasswordBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val messageHostState = rememberAppMessageHostState()
    val currentContext by rememberUpdatedState(context)
    val currentOnNavigateToLogin by rememberUpdatedState(onNavigateToLogin)

    ForgotPasswordScreen(
        state = state,
        onBackToEmailInput = { viewModel.onEvent(ForgotPasswordEvent.BackToEmailInput) },
        onEmailChange = { viewModel.onEvent(ForgotPasswordEvent.EmailChanged(it)) },
        onSubmitEmail = { viewModel.onEvent(ForgotPasswordEvent.SubmitEmail) },
        onOtpChange = { viewModel.onEvent(ForgotPasswordEvent.OtpChanged(it)) },
        onNewPasswordChange = { viewModel.onEvent(ForgotPasswordEvent.NewPasswordChanged(it)) },
        onConfirmPasswordChange = {
            viewModel.onEvent(ForgotPasswordEvent.ConfirmPasswordChanged(it))
        },
        onPasswordVisibleChange = {
            viewModel.onEvent(ForgotPasswordEvent.PasswordVisibleChanged(it))
        },
        onConfirmPasswordVisibleChange = {
            viewModel.onEvent(ForgotPasswordEvent.ConfirmPasswordVisibleChanged(it))
        },
        onResendOtp = { viewModel.onEvent(ForgotPasswordEvent.ResendOtp) },
        onSubmitResetPassword = { viewModel.onEvent(ForgotPasswordEvent.SubmitResetPassword) },
        onForgotPasswordBack = onForgotPasswordBack,
    )

    AppMessageDialogHost(
        state = messageHostState,
        onAcknowledged = { message ->
            if (message.id == ForgotPasswordMessageIds.ResetSuccess) {
                viewModel.onEvent(ForgotPasswordEvent.ResetPasswordAcknowledged)
            }
        },
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ForgotPasswordEffect.ShowErrorMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = ForgotPasswordMessageIds.Error,
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Error,
                            ),
                        )
                    }

                    is ForgotPasswordEffect.SendEmailSuccess -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = ForgotPasswordMessageIds.EmailSent,
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Success,
                            ),
                        )
                    }

                    is ForgotPasswordEffect.ResetPasswordSuccess -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = ForgotPasswordMessageIds.ResetSuccess,
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Success,
                            ),
                        )
                    }

                    is ForgotPasswordEffect.NavigateToLogin -> {
                        currentOnNavigateToLogin()
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
                        id = ForgotPasswordMessageIds.Error,
                        message = message.asString(currentContext),
                        type = AppDialogType.Error,
                    ),
                )
            }
        }
    }
}

@Composable
internal fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onBackToEmailInput: () -> Unit,
    onEmailChange: (String) -> Unit,
    onSubmitEmail: () -> Unit,
    onOtpChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    onResendOtp: () -> Unit,
    onSubmitResetPassword: () -> Unit,
    onForgotPasswordBack: () -> Unit,
) {
    val step = state.step

    BackHandler(enabled = step == ForgotPasswordStep.ResetInput) {
        onBackToEmailInput()
    }

    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
        topBar = {
            CommonHeader(
                title = stringResource(R.string.auth_forgot_password),
                onNavigateBack = {
                    if (step == ForgotPasswordStep.ResetInput) {
                        onBackToEmailInput()
                    } else {
                        onForgotPasswordBack()
                    }
                },
            )
        },
    ) { paddingValues ->
        ForgotPasswordContent(
            modifier =
            Modifier.padding(
                paddingValues = paddingValues,
            ),
            state = state,
            onEmailChange = onEmailChange,
            onSubmitEmail = onSubmitEmail,
            onOtpChange = onOtpChange,
            onNewPasswordChange = onNewPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onPasswordVisibleChange = onPasswordVisibleChange,
            onConfirmPasswordVisibleChange = onConfirmPasswordVisibleChange,
            onResendOtp = onResendOtp,
            onSubmitResetPassword = onSubmitResetPassword,
        )
    }

    AppLoadingDialog(isLoading = state.isLoading)
}

@AppPreviewLightDark
@Composable
private fun ForgotPasswordPreview() {
    TreeTaskTheme {
        ForgotPasswordScreen(
            state =
            ForgotPasswordState(
                email = "demo@gmail.com",
            ),
            onBackToEmailInput = {},
            onEmailChange = {},
            onSubmitEmail = {},
            onOtpChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onPasswordVisibleChange = {},
            onConfirmPasswordVisibleChange = {},
            onResendOtp = {},
            onSubmitResetPassword = {},
            onForgotPasswordBack = {},
        )
    }
}

@AppPreviewLightDark
@Composable
private fun ForgotPasswordResetPreview() {
    TreeTaskTheme {
        ForgotPasswordScreen(
            state =
            ForgotPasswordState(
                step = ForgotPasswordStep.ResetInput,
                email = "demo@gmail.com",
                otp = "123456",
                newPassword = "password123",
            ),
            onBackToEmailInput = {},
            onEmailChange = {},
            onSubmitEmail = {},
            onOtpChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onPasswordVisibleChange = {},
            onConfirmPasswordVisibleChange = {},
            onResendOtp = {},
            onSubmitResetPassword = {},
            onForgotPasswordBack = {},
        )
    }
}

@Composable
internal fun ForgotPasswordContent(
    state: ForgotPasswordState,
    onEmailChange: (String) -> Unit,
    onSubmitEmail: () -> Unit,
    onOtpChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    onResendOtp: () -> Unit,
    onSubmitResetPassword: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        when (state.step) {
            ForgotPasswordStep.EmailInput -> {
                EmailStep(
                    email = state.email,
                    isLoading = state.isLoading,
                    onEmailChange = onEmailChange,
                    onSubmitEmail = onSubmitEmail,
                )
            }

            ForgotPasswordStep.ResetInput -> {
                ResetPasswordStep(
                    email = state.email,
                    otp = state.otp,
                    newPassword = state.newPassword,
                    confirmPassword = state.confirmPassword,
                    passwordVisible = state.passwordVisible,
                    confirmPasswordVisible = state.confirmPasswordVisible,
                    isLoading = state.isLoading,
                    onOtpChange = onOtpChange,
                    onNewPasswordChange = onNewPasswordChange,
                    onConfirmPasswordChange = onConfirmPasswordChange,
                    onPasswordVisibleChange = onPasswordVisibleChange,
                    onConfirmPasswordVisibleChange = onConfirmPasswordVisibleChange,
                    onResendOtp = onResendOtp,
                    onSubmitResetPassword = onSubmitResetPassword,
                )
            }
        }
    }
}

private object ForgotPasswordMessageIds {
    val Error = AppMessageId("forgot-password-error")
    val EmailSent = AppMessageId("forgot-password-email-sent")
    val ResetSuccess = AppMessageId("forgot-password-reset-success")
}
