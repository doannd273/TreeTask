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
import androidx.compose.runtime.rememberUpdatedState
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
import com.doannd3.treetask.core.designsystem.component.AppLoadingDialog
import com.doannd3.treetask.core.designsystem.component.message.AppDialogType
import com.doannd3.treetask.core.designsystem.component.message.AppMessage
import com.doannd3.treetask.core.designsystem.component.message.AppMessageDialogHost
import com.doannd3.treetask.core.designsystem.component.message.AppMessageId
import com.doannd3.treetask.core.designsystem.component.message.rememberAppMessageHostState
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.designsystem.util.rememberDebouncedClick
import com.doannd3.treetask.core.model.profile.AppLanguage
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

    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current
    val messageHostState = rememberAppMessageHostState()
    val currentContext by rememberUpdatedState(context)
    val currentOnNavigateToLogin by rememberUpdatedState(onNavigateToLogin)
    val currentOnNavigateToChangePassword by rememberUpdatedState(onNavigateToChangePassword)
    val currentOnNavigateToEditProfile by rememberUpdatedState(onNavigateToEditProfile)

    ProfileScreen(
        state = state,
        onSubmitLogout = { viewModel.onEvent(ProfileEvent.SubmitLogout) },
        onOpenLanguagePicker = { viewModel.onEvent(ProfileEvent.OpenLanguagePicker) },
        onDismissLanguagePicker = { viewModel.onEvent(ProfileEvent.DismissLanguagePicker) },
        onLanguageSelect = { viewModel.onEvent(ProfileEvent.SelectLanguage(it)) },
        onNavigateToEditProfile = { viewModel.onEvent(ProfileEvent.NavigateEditProfile) },
        onNavigateToChangePassword = { viewModel.onEvent(ProfileEvent.NavigateChangePassword) },
        onDarkModeChange = { viewModel.onEvent(ProfileEvent.ToggleDarkMode(it)) },
    )

    AppMessageDialogHost(
        state = messageHostState,
        onAcknowledged = {},
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is ProfileEffect.NavigateToLogin -> {
                        currentOnNavigateToLogin()
                    }

                    is ProfileEffect.ShowErrorMessage -> {
                        messageHostState.enqueue(
                            AppMessage(
                                id = ProfileMessageIds.Error,
                                message = effect.message.asString(currentContext),
                                type = AppDialogType.Error,
                            ),
                        )
                    }

                    ProfileEffect.NavigateToChangePassword -> {
                        currentOnNavigateToChangePassword()
                    }

                    ProfileEffect.NavigateToEditProfile -> {
                        currentOnNavigateToEditProfile()
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
                        id = ProfileMessageIds.Error,
                        message = message.asString(currentContext),
                        type = AppDialogType.Error,
                    ),
                )
            }
        }
    }
}

@Composable
internal fun ProfileScreen(
    state: ProfileState,
    onSubmitLogout: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onDismissLanguagePicker: () -> Unit,
    onLanguageSelect: (AppLanguage) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing,
    ) { paddingValues ->
        ProfileContent(
            state = state,
            onSubmitLogout = onSubmitLogout,
            onOpenLanguagePicker = onOpenLanguagePicker,
            onDismissLanguagePicker = onDismissLanguagePicker,
            onLanguageSelect = onLanguageSelect,
            onNavigateToEditProfile = onNavigateToEditProfile,
            onNavigateToChangePassword = onNavigateToChangePassword,
            onDarkModeChange = onDarkModeChange,
            modifier = Modifier.padding(paddingValues),
        )
    }

    AppLoadingDialog(isLoading = state.isLoading)
}

@AppPreviewLightDark
@Composable
private fun ProfileScreenPreview() {
    TreeTaskTheme {
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
            onSubmitLogout = {},
            onOpenLanguagePicker = {},
            onDismissLanguagePicker = {},
            onLanguageSelect = {},
            onNavigateToEditProfile = {},
            onNavigateToChangePassword = {},
            onDarkModeChange = {},
        )
    }
}

@Composable
internal fun ProfileContent(
    state: ProfileState,
    onSubmitLogout: () -> Unit,
    onOpenLanguagePicker: () -> Unit,
    onDismissLanguagePicker: () -> Unit,
    onLanguageSelect: (AppLanguage) -> Unit,
    onNavigateToEditProfile: () -> Unit,
    onNavigateToChangePassword: () -> Unit,
    onDarkModeChange: (Boolean) -> Unit,
    modifier: Modifier = Modifier,
) {
    val selectedLanguage = state.selectedLanguage
    val showLanguagePicker = state.showLanguagePicker
    val isLoading = state.isLoading
    val user = state.user
    val isDarkMode = state.isDarkMode

    val onSubmitLogoutDebounced =
        rememberDebouncedClick {
            onSubmitLogout()
        }

    if (showLanguagePicker) {
        LanguagePickerBottomSheet(
            currentLanguage = selectedLanguage,
            onLanguageSelected = onLanguageSelect,
            onDismiss = onDismissLanguagePicker,
        )
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
            user?.let { profileUser ->
                ProfileHeader(
                    avatarUrl = profileUser.avatar,
                    fullName = profileUser.fullName,
                    email = profileUser.email,
                    phone = profileUser.phone,
                )
                Spacer(Modifier.height(32.dp))
            }

            ProfileSection(title = stringResource(R.string.profile_section_account)) {
                ProfileItem(
                    iconRes = R.drawable.profile_ic_edit_profile,
                    title = stringResource(R.string.profile_menu_edit_profile),
                    onClick = onNavigateToEditProfile,
                )
                ProfileItem(
                    iconRes = R.drawable.profile_ic_change_password,
                    title = stringResource(R.string.profile_menu_change_password),
                    onClick = onNavigateToChangePassword,
                )
            }

            Spacer(Modifier.height(16.dp))

            ProfileSection(title = stringResource(R.string.profile_section_settings)) {
                ProfileSwitchItem(
                    iconRes = R.drawable.profile_ic_dark_mode,
                    title = stringResource(R.string.profile_menu_dark_mode),
                    checked = isDarkMode,
                    onCheckedChange = onDarkModeChange,
                )
                ProfileItem(
                    iconRes = R.drawable.profile_ic_language,
                    title = stringResource(R.string.profile_menu_language),
                    trailing = {
                        Text(
                            text = stringResource(selectedLanguage.displayNameResId()),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                    onClick = onOpenLanguagePicker,
                )
            }
        }

        Spacer(Modifier.height(16.dp))

        LogoutButton(
            isEnable = !isLoading,
            onSubmitLogout = onSubmitLogoutDebounced,
        )
    }
}

private object ProfileMessageIds {
    val Error = AppMessageId("profile-error")
}
