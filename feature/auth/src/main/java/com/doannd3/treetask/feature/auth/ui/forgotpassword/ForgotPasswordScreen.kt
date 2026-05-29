package com.doannd3.treetask.feature.auth.ui.forgotpassword

import androidx.activity.compose.BackHandler
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.CommonHeader
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
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

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    ForgotPasswordScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onForgotPasswordBack = onForgotPasswordBack,
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ForgotPasswordEffect.ShowErrorMessage -> {
                        val errorStr = effect.message.asString(context)
                        globalAppState.showError(errorStr)
                    }

                    is ForgotPasswordEffect.SendEmailSuccess -> {
                        val successForgotPassword = effect.message.asString(context)
                        globalAppState.showSuccess(
                            message = successForgotPassword,
                        )
                    }

                    is ForgotPasswordEffect.ResetPasswordSuccess -> {
                        val successForgotPassword = effect.message.asString(context)
                        globalAppState.showSuccess(
                            message = successForgotPassword,
                            onDismiss = {
                                viewModel.onEvent(ForgotPasswordEvent.ResetPasswordAcknowledged)
                            },
                        )
                    }

                    is ForgotPasswordEffect.NavigateToLogin -> onNavigateToLogin()
                }
            }
        }
    }

    LaunchedEffect(viewModel.baseErrorEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.baseErrorEffect.collect { message ->
                globalAppState.showError(message.asString(context))
            }
        }
    }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) {
            globalAppState.showLoading()
        } else {
            globalAppState.hideLoading()
        }
    }
}

@Composable
internal fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onEvent: (ForgotPasswordEvent) -> Unit,
    onForgotPasswordBack: () -> Unit,
) {
    BackHandler(enabled = state.step == ForgotPasswordStep.ResetInput) {
        onEvent(ForgotPasswordEvent.BackToEmailInput)
    }

    Scaffold(
        contentWindowInsets =
            WindowInsets.safeDrawing.only(
                WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
            ),
        topBar = {
            CommonHeader(
                title = stringResource(R.string.auth_forgot_password),
                onNavigateBack = {
                    if (state.step == ForgotPasswordStep.ResetInput) {
                        onEvent(ForgotPasswordEvent.BackToEmailInput)
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
            onEvent = onEvent,
        )
    }
}

@Composable
internal fun ForgotPasswordContent(
    modifier: Modifier = Modifier,
    state: ForgotPasswordState,
    onEvent: (ForgotPasswordEvent) -> Unit,
) {
    Column(
        modifier =
            modifier
                .fillMaxSize()
                .verticalScroll(rememberScrollState())
                .imePadding(),
    ) {
        EmailStep(
            isVisible = (state.step == ForgotPasswordStep.EmailInput),
            state = state,
            onEvent = onEvent,
        )
        ResetPasswordStep(
            isVisible = (state.step == ForgotPasswordStep.ResetInput),
            state = state,
            onEvent = onEvent,
        )
    }
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
            onEvent = {},
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
            onEvent = {},
            onForgotPasswordBack = {},
        )
    }
}
