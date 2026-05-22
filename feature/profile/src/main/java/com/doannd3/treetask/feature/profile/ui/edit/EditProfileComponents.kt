package com.doannd3.treetask.feature.profile.ui.edit

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.core.designsystem.component.CommonButton
import com.doannd3.treetask.core.designsystem.component.EmailInput
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.designsystem.util.rememberDebouncedClick
import com.doannd3.treetask.feature.profile.R

// region EditProfileForm

@Composable
fun EditProfileForm(
    state: EditProfileState,
    onEvent: (EditProfileEvent) -> Unit,
) {
    val emailFocusRequester = remember { FocusRequester() }
    val phoneFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current
    val onSaveDebounced = rememberDebouncedClick {
        onEvent(EditProfileEvent.SaveClicked)
    }

    Column(
        modifier =
        Modifier
            .fillMaxWidth()
            .padding(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp),
    ) {
        OutlinedTextField(
            modifier = Modifier.fillMaxWidth(),
            value = state.fullName,
            onValueChange = { onEvent(EditProfileEvent.FullNameChanged(it)) },
            label = { Text(text = stringResource(R.string.profile_edit_full_name_label)) },
            enabled = !state.isLoading,
            singleLine = true,
            keyboardOptions =
            KeyboardOptions(
                imeAction = ImeAction.Next,
            ),
            keyboardActions =
            KeyboardActions(
                onNext = { emailFocusRequester.requestFocus() },
            ),
        )

        EmailInput(
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(emailFocusRequester),
            label = stringResource(R.string.profile_edit_email_label),
            email = state.email,
            isEnable = !state.isLoading,
            onEmailChange = { onEvent(EditProfileEvent.EmailChanged(it)) },
            imeAction = ImeAction.Next,
            onImeNext = { phoneFocusRequester.requestFocus() },
        )

        OutlinedTextField(
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(phoneFocusRequester),
            value = state.phone,
            onValueChange = { onEvent(EditProfileEvent.PhoneChanged(it)) },
            label = { Text(text = stringResource(R.string.profile_edit_phone_label)) },
            enabled = !state.isLoading,
            singleLine = true,
            keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Phone,
                imeAction = ImeAction.Done,
            ),
            keyboardActions =
            KeyboardActions(
                onDone = {
                    focusManager.clearFocus()
                    onSaveDebounced()
                },
            ),
        )

        Spacer(modifier = Modifier.height(8.dp))

        CommonButton(
            buttonText = stringResource(R.string.profile_edit_save_action),
            isEnable = !state.isLoading,
            onSubmit = onSaveDebounced,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun EditProfileFormPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            EditProfileForm(
                state =
                EditProfileState(
                    fullName = stringResource(R.string.profile_preview_full_name),
                    email = stringResource(R.string.profile_preview_email),
                    phone = stringResource(R.string.profile_preview_phone),
                ),
                onEvent = {},
            )
        }
    }
}

// endregion
