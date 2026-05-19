package com.doannd3.treetask.feature.profile.ui.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
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
) {
    var isDarkTheme by remember { mutableStateOf(false) }

    TreeTaskTheme(darkTheme = isDarkTheme) {
        Scaffold(
            contentWindowInsets = WindowInsets.safeDrawing,
        ) { paddingValues ->
            ProfileContent(
                modifier = Modifier.padding(paddingValues),
                isDarkTheme = isDarkTheme,
                onToggleDarkTheme = { isDarkTheme = it },
                state = state,
                onEvent = onEvent,
            )
        }
    }
}

@Composable
fun ProfileContent(
    modifier: Modifier = Modifier,
    isDarkTheme: Boolean = false,
    onToggleDarkTheme: (Boolean) -> Unit = {},
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
) {
    val onSubmitLogoutDebounced =
        rememberDebouncedClick {
            onEvent(ProfileEvent.SubmitLogout)
        }

    Column(modifier = modifier.padding(16.dp)) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Text(
                text = if (isDarkTheme) "Dark mode" else "Light mode",
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
            )
            Switch(
                checked = isDarkTheme,
                onCheckedChange = onToggleDarkTheme,
            )
        }

        LogoutButton(
            isEnable = !state.isLoading,
            onSubmitLogout = onSubmitLogoutDebounced,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    TreeTaskTheme {
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
        )
    }
}
