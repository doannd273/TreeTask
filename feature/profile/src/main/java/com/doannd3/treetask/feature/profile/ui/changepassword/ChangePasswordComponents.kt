package com.doannd3.treetask.feature.profile.ui.changepassword

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.core.designsystem.component.CommonButton
import com.doannd3.treetask.core.designsystem.component.PasswordInput
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.designsystem.util.rememberDebouncedClick
import com.doannd3.treetask.feature.profile.R

// region ChangePasswordForm

@Composable
internal fun ChangePasswordForm(
    state: ChangePasswordState,
    onEvent: (ChangePasswordEvent) -> Unit,
) {
    val currentPasswordFocusRequester = remember { FocusRequester() }
    val newPasswordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val onSubmitDebounced =
        rememberDebouncedClick {
            onEvent(ChangePasswordEvent.SubmitChangePassword)
        }

    Column(
        modifier =
            Modifier
                .fillMaxWidth()
                .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        PasswordInput(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(currentPasswordFocusRequester),
            label = stringResource(R.string.profile_change_password_current_label),
            password = state.currentPassword,
            passwordVisible = state.currentPasswordVisible,
            enabled = !state.isLoading,
            imeAction = ImeAction.Next,
            onImeNext = { newPasswordFocusRequester.requestFocus() },
            onPasswordChange = { onEvent(ChangePasswordEvent.CurrentPasswordChanged(it)) },
            onPasswordVisibleChange = { onEvent(ChangePasswordEvent.CurrentPasswordVisibleChanged(it)) },
        )

        PasswordInput(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(newPasswordFocusRequester),
            label = stringResource(R.string.profile_change_password_new_label),
            password = state.newPassword,
            passwordVisible = state.newPasswordVisible,
            enabled = !state.isLoading,
            imeAction = ImeAction.Next,
            onImeNext = { confirmPasswordFocusRequester.requestFocus() },
            onPasswordChange = { onEvent(ChangePasswordEvent.NewPasswordChanged(it)) },
            onPasswordVisibleChange = { onEvent(ChangePasswordEvent.NewPasswordVisibleChanged(it)) },
        )

        PasswordInput(
            modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(confirmPasswordFocusRequester),
            label = stringResource(R.string.profile_change_password_confirm_label),
            password = state.confirmPassword,
            passwordVisible = state.confirmPasswordVisible,
            enabled = !state.isLoading,
            imeAction = ImeAction.Done,
            onImeDone = {
                focusManager.clearFocus()
                onSubmitDebounced()
            },
            onPasswordChange = { onEvent(ChangePasswordEvent.ConfirmPasswordChanged(it)) },
            onPasswordVisibleChange = { onEvent(ChangePasswordEvent.ConfirmPasswordVisibleChanged(it)) },
        )

        Spacer(modifier = Modifier.height(8.dp))

        CommonButton(
            buttonText = stringResource(R.string.profile_change_password_update_action),
            isEnable = !state.isLoading,
            onSubmit = onSubmitDebounced,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun ChangePasswordFormPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ChangePasswordForm(
                state = ChangePasswordState(),
                onEvent = {},
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun ChangePasswordFormFilledPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ChangePasswordForm(
                state =
                    ChangePasswordState(
                        currentPassword = stringResource(R.string.profile_preview_current_password),
                        newPassword = stringResource(R.string.profile_preview_new_password),
                        confirmPassword = stringResource(R.string.profile_preview_new_password),
                    ),
                onEvent = {},
            )
        }
    }
}

// endregion
