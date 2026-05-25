package com.doannd3.treetask.feature.profile.ui.edit

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
import com.doannd3.treetask.feature.profile.R

@Composable
fun EditProfileRoute(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current

    EditProfileScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    EditProfileEffect.NavigateBack -> {
                        onNavigateBack()
                    }

                    is EditProfileEffect.ShowErrorMessage -> {
                        globalAppState.showError(effect.message.asString(context))
                    }

                    is EditProfileEffect.ShowSuccessMessage -> {
                        globalAppState.showSuccess(effect.message.asString(context)) {
                            viewModel.onEvent(EditProfileEvent.SuccessAcknowledged)
                        }
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
internal fun EditProfileScreen(
    state: EditProfileState,
    onEvent: (EditProfileEvent) -> Unit,
) {
    Scaffold(
        contentWindowInsets =
        WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
        ),
        topBar = {
            CommonHeader(
                title = stringResource(R.string.profile_edit_title),
                onNavigateBack = { onEvent(EditProfileEvent.BackClicked) },
            )
        },
    ) { paddingValues ->
        EditProfileContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onEvent = onEvent,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun EditProfileScreenPreview() {
    TreeTaskTheme {
        EditProfileScreen(
            state = EditProfileState(),
            onEvent = {},
        )
    }
}

@Composable
internal fun EditProfileContent(
    modifier: Modifier = Modifier,
    state: EditProfileState,
    onEvent: (EditProfileEvent) -> Unit,
) {
    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        EditProfileForm(
            state = state,
            onEvent = onEvent,
        )
    }
}
