package com.doannd3.treetask.feature.profile.ui.profile

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.util.rememberDebouncedClick
import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.feature.profile.R

@Composable
fun ProfileRoute(
    viewModel: ProfileViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onNavigateToEditProfile: () -> Unit,
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

                    ProfileEffect.NavigateToChangePassword -> {
                        onNavigateToChangePassword()
                    }

                    ProfileEffect.NavigateToEditProfile -> {
                        onNavigateToEditProfile()
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
internal fun ProfileScreen(
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingValues ->
        ProfileContent(
            modifier = Modifier.padding(paddingValues),
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
internal fun ProfileContent(
    modifier: Modifier = Modifier,
    state: ProfileState,
    onEvent: (ProfileEvent) -> Unit,
) {
    val onSubmitLogoutDebounced =
        rememberDebouncedClick {
            onEvent(ProfileEvent.SubmitLogout)
        }

    if (state.showLanguagePicker) {
        LanguagePickerBottomSheet(
            currentLanguage = state.selectedLanguage,
            onLanguageSelected = { onEvent(ProfileEvent.ConfirmLanguage(it)) },
            onDismiss = { onEvent(ProfileEvent.DismissLanguagePicker) },
        )

//        LanguagePickerDialog(
//            modifier = Modifier,
//            currentLanguage = state.selectedLanguage,
//            onConfirm = { onEvent(ProfileEvent.ConfirmLanguage(it)) },
//            onDismiss = { onEvent(ProfileEvent.DismissLanguagePicker) },
//        )
    }

    Column(
        modifier =
            modifier
                .fillMaxSize()
                .padding(horizontal = 16.dp, vertical = 24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        Column(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .verticalScroll(rememberScrollState()),
            horizontalAlignment = Alignment.CenterHorizontally,
        ) {
            state.user?.let { user ->
                ProfileHeader(
                    avatarUrl = user.avatar,
                    fullName = user.fullName,
                    email = user.email,
                    phone = user.phone,
                )
                Spacer(Modifier.height(32.dp))
            }

            ProfileSection(title = stringResource(R.string.profile_section_account)) {
                ProfileItem(
                    iconRes = R.drawable.profile_ic_edit_profile,
                    title = stringResource(R.string.profile_menu_edit_profile),
                    onClick = { onEvent(ProfileEvent.NavigateEditProfile) },
                )
                ProfileItem(
                    iconRes = R.drawable.profile_ic_change_password,
                    title = stringResource(R.string.profile_menu_change_password),
                    onClick = { onEvent(ProfileEvent.NavigateChangePassword) },
                )
            }

            Spacer(Modifier.height(16.dp))

            ProfileSection(title = stringResource(R.string.profile_section_settings)) {
                ProfileSwitchItem(
                    iconRes = R.drawable.profile_ic_dark_mode,
                    title = stringResource(R.string.profile_menu_dark_mode),
                    checked = state.isDarkMode,
                    onCheckedChange = { onEvent(ProfileEvent.ToggleDarkMode(it)) },
                )
                ProfileItem(
                    iconRes = R.drawable.profile_ic_language,
                    title = stringResource(R.string.profile_menu_language),
                    trailing = {
                        Text(
                            text = stringResource(state.selectedLanguage.displayNameResId()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    onClick = { onEvent(ProfileEvent.OpenLanguagePicker) },
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        LogoutButton(
            isEnable = !state.isLoading,
            onSubmitLogout = onSubmitLogoutDebounced,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    ProfileScreen(
        state =
            ProfileState(
                isLoading = false,
                isDarkMode = false,
                user =
                    User(
                        id = stringResource(R.string.profile_preview_user_id),
                        email = stringResource(R.string.profile_preview_email),
                        fullName = stringResource(R.string.profile_preview_full_name),
                        avatar = null,
                        phone = stringResource(R.string.profile_preview_phone),
                    ),
            ),
        onEvent = {},
    )
}
