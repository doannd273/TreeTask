package com.doannd3.treetask.feature.auth.ui.forgotpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.component.CommonButton
import com.doannd3.treetask.core.designsystem.component.EmailInput
import com.doannd3.treetask.core.designsystem.component.OtpInput
import com.doannd3.treetask.core.designsystem.component.PasswordInput
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.designsystem.util.rememberDebouncedClick
import com.doannd3.treetask.feature.auth.R

// region EmailStep

@Composable
internal fun EmailStep(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    state: ForgotPasswordState,
    onEvent: (ForgotPasswordEvent) -> Unit,
) {
    if (isVisible) {
        val onSubmitSendEmailDebounced = rememberDebouncedClick {
            onEvent(ForgotPasswordEvent.SubmitEmail)
        }

        Column(
            modifier = modifier.padding(16.dp),
        ) {
            EmailInput(
                modifier = Modifier.fillMaxWidth(),
                label = stringResource(R.string.auth_email_hint),
                email = state.email,
                isEnable = state.step == ForgotPasswordStep.EmailInput,
                onEmailChange = { onEvent(ForgotPasswordEvent.EmailChanged(it)) },
                imeAction = ImeAction.Done,
                onImeDone = { onSubmitSendEmailDebounced() },
            )

            Spacer(modifier = Modifier.height(16.dp))

            CommonButton(
                buttonText = stringResource(R.string.auth_send_otp),
                isEnable = !state.isLoading,
                onSubmit = onSubmitSendEmailDebounced,
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun EmailStepPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            EmailStep(
                state = ForgotPasswordState(
                    email = "doan@gmail.com",
                    step = ForgotPasswordStep.EmailInput,
                    isLoading = false,
                ),
                onEvent = {},
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun EmailStepLoadingPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            EmailStep(
                state = ForgotPasswordState(
                    email = "doan@gmail.com",
                    step = ForgotPasswordStep.EmailInput,
                    isLoading = true,
                ),
                onEvent = {},
            )
        }
    }
}

// endregion

// region ResetPasswordStep

@Composable
internal fun ResetPasswordStep(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    state: ForgotPasswordState,
    onEvent: (ForgotPasswordEvent) -> Unit,
) {
    if (isVisible) {
        val onSubmitResetPasswordDebounced = rememberDebouncedClick {
            onEvent(ForgotPasswordEvent.SubmitResetPassword)
        }
        val onResendOtpDebounced = rememberDebouncedClick {
            onEvent(ForgotPasswordEvent.ResendOtp)
        }
        val passwordFocusRequester = remember { FocusRequester() }
        val confirmPasswordFocusRequester = remember { FocusRequester() }

        Column(
            modifier = modifier.padding(16.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.auth_otp_sent_to_email, state.email),
                color = MaterialTheme.colorScheme.primary,
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OtpInput(
                value = state.otp,
                onValueChange = { onEvent(ForgotPasswordEvent.OtpChanged(it)) },
                onOtpComplete = { passwordFocusRequester.requestFocus() },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                label = stringResource(R.string.auth_new_password_hint),
                password = state.newPassword,
                passwordVisible = state.passwordVisible,
                onPasswordChange = { onEvent(ForgotPasswordEvent.NewPasswordChanged(it)) },
                onPasswordVisibleChange = { onEvent(ForgotPasswordEvent.PasswordVisibleChanged(it)) },
                imeAction = ImeAction.Next,
                onImeNext = { confirmPasswordFocusRequester.requestFocus() },
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(confirmPasswordFocusRequester),
                label = stringResource(R.string.auth_confirm_password_hint),
                password = state.confirmPassword,
                passwordVisible = state.confirmPasswordVisible,
                onPasswordChange = { onEvent(ForgotPasswordEvent.ConfirmPasswordChanged(it)) },
                onPasswordVisibleChange = { onEvent(ForgotPasswordEvent.ConfirmPasswordVisibleChanged(it)) },
                imeAction = ImeAction.Done,
                onImeDone = { onSubmitResetPasswordDebounced() },
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                onClick = onResendOtpDebounced,
            ) {
                Text(
                    text = stringResource(R.string.auth_resend_otp),
                    color = MaterialTheme.colorScheme.primary,
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            CommonButton(
                buttonText = stringResource(R.string.auth_reset_password),
                isEnable = !state.isLoading,
                onSubmit = onSubmitResetPasswordDebounced,
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun ResetPasswordStepPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ResetPasswordStep(
                state = ForgotPasswordState(
                    step = ForgotPasswordStep.ResetInput,
                    email = "doan@gmail.com",
                    otp = "123",
                    newPassword = "",
                    isLoading = false,
                    confirmPassword = "",
                ),
                onEvent = {},
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun ResetPasswordStepFilledPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ResetPasswordStep(
                state = ForgotPasswordState(
                    step = ForgotPasswordStep.ResetInput,
                    email = "doan@gmail.com",
                    otp = "123456",
                    newPassword = "password123",
                    passwordVisible = false,
                    isLoading = false,
                    confirmPassword = "password124",
                    confirmPasswordVisible = false,
                ),
                onEvent = {},
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun ResetPasswordStepLoadingPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ResetPasswordStep(
                state = ForgotPasswordState(
                    step = ForgotPasswordStep.ResetInput,
                    email = "doan@gmail.com",
                    otp = "123456",
                    newPassword = "password123",
                    isLoading = true,
                ),
                onEvent = {},
            )
        }
    }
}

// endregion
