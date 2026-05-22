package com.doannd3.treetask.feature.profile.ui.changepassword

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
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.CommonHeader
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.feature.profile.R

@Composable
fun ChangePasswordRoute(
    viewModel: ChangePasswordViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    ChangePasswordScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onNavigateBack = onNavigateBack,
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ChangePasswordEffect.ShowErrorMessage -> {
                        globalAppState.showError(effect.message.asString(context))
                    }

                    is ChangePasswordEffect.ShowSuccessMessage -> {
                        globalAppState.showSuccess(effect.message.asString(context)) {
                            viewModel.onEvent(ChangePasswordEvent.SuccessAcknowledged)
                        }
                    }

                    is ChangePasswordEffect.NavigateBack -> {
                        onNavigateBack()
                    }
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
internal fun ChangePasswordScreen(
    state: ChangePasswordState,
    onEvent: (ChangePasswordEvent) -> Unit,
    onNavigateBack: () -> Unit,
) {
    Scaffold(
        contentWindowInsets =
        WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
        ),
        topBar = {
            CommonHeader(
                title = stringResource(R.string.profile_change_password_title),
                onNavigateBack = onNavigateBack,
            )
        },
    ) { paddingValues ->
        ChangePasswordContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
internal fun ChangePasswordContent(
    modifier: Modifier = Modifier,
    state: ChangePasswordState,
    onEvent: (ChangePasswordEvent) -> Unit,
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
            onEvent = onEvent,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun ChangePasswordScreenPreview() {
    TreeTaskTheme {
        ChangePasswordScreen(
            state = ChangePasswordState(),
            onEvent = {},
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
            onEvent = {},
            onNavigateBack = {},
        )
    }
}
