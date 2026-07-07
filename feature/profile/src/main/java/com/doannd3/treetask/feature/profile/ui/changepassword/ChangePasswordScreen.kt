package com.doannd3.treetask.feature.profile.ui.changepassword

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
import com.doannd3.treetask.feature.profile.R

@Composable
fun ChangePasswordRoute(
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val messageHostState = rememberAppMessageHostState()
    val currentContext by rememberUpdatedState(context)
    val currentOnNavigateBack by rememberUpdatedState(onNavigateBack)

    ChangePasswordScreen(
        state = state,
        onCurrentPasswordChange = { viewModel.onEvent(ChangePasswordEvent.CurrentPasswordChanged(it)) },
        onNewPasswordChange = { viewModel.onEvent(ChangePasswordEvent.NewPasswordChanged(it)) },
        onConfirmPasswordChange = { viewModel.onEvent(ChangePasswordEvent.ConfirmPasswordChanged(it)) },
        onCurrentPasswordVisibleChange = {
            viewModel.onEvent(ChangePasswordEvent.CurrentPasswordVisibleChanged(it))
        },
        onNewPasswordVisibleChange = {
            viewModel.onEvent(ChangePasswordEvent.NewPasswordVisibleChanged(it))
        },
        onConfirmPasswordVisibleChange = {
            viewModel.onEvent(ChangePasswordEvent.ConfirmPasswordVisibleChanged(it))
        },
        onSubmitChangePassword = { viewModel.onEvent(ChangePasswordEvent.SubmitChangePassword) },
        onNavigateBack = onNavigateBack,
    )

    AppMessageDialogHost(
        state = messageHostState,
        onAcknowledged = { message ->
            if (message.id == ChangePasswordMessageIds.Success) {
                viewModel.onEvent(ChangePasswordEvent.SuccessAcknowledged)
            }
        },
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ChangePasswordEffect.ShowErrorMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = ChangePasswordMessageIds.Error,
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Error,
                            ),
                        )
                    }

                    is ChangePasswordEffect.ShowSuccessMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = ChangePasswordMessageIds.Success,
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Success,
                            ),
                        )
                    }

                    is ChangePasswordEffect.NavigateBack -> {
                        currentOnNavigateBack()
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
                        id = ChangePasswordMessageIds.Error,
                        message = message.asString(currentContext),
                        type = AppDialogType.Error,
                    ),
                )
            }
        }
    }
}

@Composable
internal fun ChangePasswordScreen(
    state: ChangePasswordState,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onCurrentPasswordVisibleChange: (Boolean) -> Unit,
    onNewPasswordVisibleChange: (Boolean) -> Unit,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    onSubmitChangePassword: () -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        contentWindowInsets =
        WindowInsets.safeDrawing,
        topBar = {
            CommonHeader(
                title = stringResource(R.string.profile_change_password_title),
                onNavigateBack = onNavigateBack,
            )
        },
    ) { paddingValues ->
        ChangePasswordContent(
            state = state,
            onCurrentPasswordChange = onCurrentPasswordChange,
            onNewPasswordChange = onNewPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onCurrentPasswordVisibleChange = onCurrentPasswordVisibleChange,
            onNewPasswordVisibleChange = onNewPasswordVisibleChange,
            onConfirmPasswordVisibleChange = onConfirmPasswordVisibleChange,
            onSubmitChangePassword = onSubmitChangePassword,
            modifier = Modifier.padding(paddingValues),
        )
    }

    AppLoadingDialog(isLoading = state.isLoading)
}

@AppPreviewLightDark
@Composable
private fun ChangePasswordScreenPreview() {
    TreeTaskTheme {
        ChangePasswordScreen(
            state = ChangePasswordState(),
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onCurrentPasswordVisibleChange = {},
            onNewPasswordVisibleChange = {},
            onConfirmPasswordVisibleChange = {},
            onSubmitChangePassword = {},
            onNavigateBack = {},
        )
    }
}

@AppPreviewLightDark
@Composable
private fun ChangePasswordScreenFilledPreview() {
    TreeTaskTheme {
        ChangePasswordScreen(
            state =
            ChangePasswordState(
                currentPassword = stringResource(R.string.profile_preview_current_password),
                newPassword = stringResource(R.string.profile_preview_new_password),
                confirmPassword = stringResource(R.string.profile_preview_new_password),
            ),
            onCurrentPasswordChange = {},
            onNewPasswordChange = {},
            onConfirmPasswordChange = {},
            onCurrentPasswordVisibleChange = {},
            onNewPasswordVisibleChange = {},
            onConfirmPasswordVisibleChange = {},
            onSubmitChangePassword = {},
            onNavigateBack = {},
        )
    }
}

@Composable
internal fun ChangePasswordContent(
    state: ChangePasswordState,
    onCurrentPasswordChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onCurrentPasswordVisibleChange: (Boolean) -> Unit,
    onNewPasswordVisibleChange: (Boolean) -> Unit,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    onSubmitChangePassword: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        ChangePasswordForm(
            state = state,
            onCurrentPasswordChange = onCurrentPasswordChange,
            onNewPasswordChange = onNewPasswordChange,
            onConfirmPasswordChange = onConfirmPasswordChange,
            onCurrentPasswordVisibleChange = onCurrentPasswordVisibleChange,
            onNewPasswordVisibleChange = onNewPasswordVisibleChange,
            onConfirmPasswordVisibleChange = onConfirmPasswordVisibleChange,
            onSubmitChangePassword = onSubmitChangePassword,
        )
    }
}

private object ChangePasswordMessageIds {
    val Error = AppMessageId("change-password-error")
    val Success = AppMessageId("change-password-success")
}
