package com.doannd3.treetask.feature.profile.ui.profile

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.designsystem.util.rememberDebouncedClick
import com.doannd3.treetask.core.model.user.User

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    ProfileScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onUploadAvatarUser = {},
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ProfileEffect.NavigateToLogin -> {
                        onNavigateToLogin()
                    }

                    is ProfileEffect.ShowErrorMessage -> {
                        val errorStr = effect.message.asString(context)
                        globalAppState.showError(errorStr)
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
fun ProfileScreen(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    onUploadAvatarUser: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingValues ->
        ProfileContent(
            modifier = Modifier.padding(paddingValues = paddingValues),
            state = state,
            onEvent = onEvent,
            onUploadAvatarUser = onUploadAvatarUser,
        )
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
    onUploadAvatarUser: () -> Unit,
) {
    val onSubmitLogoutDebounced =
        rememberDebouncedClick {
            onEvent(ProfileEvent.SubmitLogout)
        }

    LogoutButton(
        isEnable = !state.isLoading,
        onSubmitLogout = onSubmitLogoutDebounced,
    )
}

@Composable
@Preview(showBackground = true)
fun ProfileScreenPreview() {
    ProfileScreen(
        state =
        ProfileState(
            isLoading = false,
            user =
            User(
                id = "user_preview",
                email = "doan@example.com",
                fullName = "Doan Nguyen",
                avatar = null,
                phone = "+84 900 000 000",
            ),
        ),
        onEvent = {},
        onUploadAvatarUser = {},
    )
}
