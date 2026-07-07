package com.doannd3.treetask.feature.profile.ui.edit

import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
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
fun EditProfileRoute(
    viewModel: EditProfileViewModel = hiltViewModel(),
    onNavigateBack: () -> Unit,
) {
    val context = LocalContext.current
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val lifecycleOwner = LocalLifecycleOwner.current
    val messageHostState = rememberAppMessageHostState()

    EditProfileScreen(
        state = state,
        onEvent = viewModel::onEvent,
    )

    AppMessageDialogHost(
        state = messageHostState,
        onAcknowledged = { message ->
            if (message.id == EditProfileMessageIds.Success) {
                viewModel.onEvent(EditProfileEvent.SuccessAcknowledged)
            }
        },
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    EditProfileEffect.NavigateBack -> {
                        onNavigateBack()
                    }

                    is EditProfileEffect.ShowErrorMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = EditProfileMessageIds.Error,
                                message = effect.message.asString(context),
                                type = AppDialogType.Error,
                            ),
                        )
                    }

                    is EditProfileEffect.ShowSuccessMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = EditProfileMessageIds.Success,
                                message = effect.message.asString(context),
                                type = AppDialogType.Success,
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
                        id = EditProfileMessageIds.Error,
                        message = message.asString(context),
                        type = AppDialogType.Error,
                    ),
                )
            }
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
        WindowInsets.safeDrawing,
        topBar = {
            CommonHeader(
                title = stringResource(R.string.profile_edit_title),
                onNavigateBack = { onEvent(EditProfileEvent.BackClicked) },
            )
        },
    ) { paddingValues ->
        EditProfileContent(
            state = state,
            onEvent = onEvent,
            modifier = Modifier.padding(paddingValues),
        )
    }

    AppLoadingDialog(isLoading = state.isLoading)
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
    state: EditProfileState,
    onEvent: (EditProfileEvent) -> Unit,
    modifier: Modifier = Modifier,
) {
    val launcher =
        rememberLauncherForActivityResult(
            contract = ActivityResultContracts.PickVisualMedia(),
        ) { uri ->
            if (uri != null) {
                onEvent(EditProfileEvent.AvatarChanged(avatarUri = uri))
            }
        }

    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        AvatarPicker(
            isEnable = !state.isLoading,
            avatarUri = state.avatarUri,
            avatarUrl = state.avatarUrl,
            avatarClick = {
                launcher.launch(
                    PickVisualMediaRequest(
                        ActivityResultContracts.PickVisualMedia.ImageOnly,
                    ),
                )
            },
        )

        EditProfileForm(
            state = state,
            onEvent = onEvent,
        )
    }
}

private object EditProfileMessageIds {
    val Error = AppMessageId("edit-profile-error")
    val Success = AppMessageId("edit-profile-success")
}
