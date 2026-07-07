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
    email: String,
    isLoading: Boolean,
    onEmailChange: (String) -> Unit,
    onSubmitEmail: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val onSubmitSendEmailDebounced =
        rememberDebouncedClick {
            onSubmitEmail()
        }

    Column(
        modifier = modifier.padding(16.dp),
    ) {
        EmailInput(
            modifier = Modifier.fillMaxWidth(),
            label = stringResource(R.string.auth_email_hint),
            email = email,
            onEmailChange = onEmailChange,
            imeAction = ImeAction.Done,
            onImeDone = { onSubmitSendEmailDebounced() },
        )

        Spacer(modifier = Modifier.height(16.dp))

        CommonButton(
            buttonText = stringResource(R.string.auth_send_otp),
            isEnable = !isLoading,
            onSubmit = onSubmitSendEmailDebounced,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun EmailStepPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            EmailStep(
                email = "doan@gmail.com",
                isLoading = false,
                onEmailChange = {},
                onSubmitEmail = {},
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
                email = "doan@gmail.com",
                isLoading = true,
                onEmailChange = {},
                onSubmitEmail = {},
            )
        }
    }
}

// endregion

// region ResetPasswordStep

@Composable
internal fun ResetPasswordStep(
    email: String,
    otp: String,
    newPassword: String,
    confirmPassword: String,
    passwordVisible: Boolean,
    confirmPasswordVisible: Boolean,
    isLoading: Boolean,
    onOtpChange: (String) -> Unit,
    onNewPasswordChange: (String) -> Unit,
    onConfirmPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onConfirmPasswordVisibleChange: (Boolean) -> Unit,
    onResendOtp: () -> Unit,
    onSubmitResetPassword: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val onSubmitResetPasswordDebounced =
        rememberDebouncedClick {
            onSubmitResetPassword()
        }
    val onResendOtpDebounced =
        rememberDebouncedClick {
            onResendOtp()
        }
    val passwordFocusRequester = remember { FocusRequester() }
    val confirmPasswordFocusRequester = remember { FocusRequester() }

    Column(
        modifier = modifier.padding(16.dp),
    ) {
        Text(
            modifier = Modifier.fillMaxWidth(),
            text = stringResource(R.string.auth_otp_sent_to_email, email),
            color = MaterialTheme.colorScheme.primary,
            fontSize = 14.sp,
        )

        Spacer(modifier = Modifier.height(16.dp))

        OtpInput(
            value = otp,
            onValueChange = onOtpChange,
            onOtpComplete = { passwordFocusRequester.requestFocus() },
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordInput(
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(passwordFocusRequester),
            label = stringResource(R.string.auth_new_password_hint),
            password = newPassword,
            passwordVisible = passwordVisible,
            onPasswordChange = onNewPasswordChange,
            onPasswordVisibleChange = onPasswordVisibleChange,
            imeAction = ImeAction.Next,
            onImeNext = { confirmPasswordFocusRequester.requestFocus() },
        )

        Spacer(modifier = Modifier.height(16.dp))

        PasswordInput(
            modifier =
            Modifier
                .fillMaxWidth()
                .focusRequester(confirmPasswordFocusRequester),
            label = stringResource(R.string.auth_confirm_password_hint),
            password = confirmPassword,
            passwordVisible = confirmPasswordVisible,
            onPasswordChange = onConfirmPasswordChange,
            onPasswordVisibleChange = onConfirmPasswordVisibleChange,
            imeAction = ImeAction.Done,
            onImeDone = { onSubmitResetPasswordDebounced() },
        )

        Spacer(modifier = Modifier.height(16.dp))

        TextButton(
            modifier = Modifier.fillMaxWidth(),
            enabled = !isLoading,
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
            isEnable = !isLoading,
            onSubmit = onSubmitResetPasswordDebounced,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun ResetPasswordStepPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ResetPasswordStep(
                email = "doan@gmail.com",
                otp = "123",
                newPassword = "",
                confirmPassword = "",
                passwordVisible = false,
                confirmPasswordVisible = false,
                isLoading = false,
                onOtpChange = {},
                onNewPasswordChange = {},
                onConfirmPasswordChange = {},
                onPasswordVisibleChange = {},
                onConfirmPasswordVisibleChange = {},
                onResendOtp = {},
                onSubmitResetPassword = {},
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
                email = "doan@gmail.com",
                otp = "123456",
                newPassword = "password123",
                confirmPassword = "password124",
                passwordVisible = false,
                confirmPasswordVisible = false,
                isLoading = false,
                onOtpChange = {},
                onNewPasswordChange = {},
                onConfirmPasswordChange = {},
                onPasswordVisibleChange = {},
                onConfirmPasswordVisibleChange = {},
                onResendOtp = {},
                onSubmitResetPassword = {},
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
                email = "doan@gmail.com",
                otp = "123456",
                newPassword = "password123",
                confirmPassword = "",
                passwordVisible = false,
                confirmPasswordVisible = false,
                isLoading = true,
                onOtpChange = {},
                onNewPasswordChange = {},
                onConfirmPasswordChange = {},
                onPasswordVisibleChange = {},
                onConfirmPasswordVisibleChange = {},
                onResendOtp = {},
                onSubmitResetPassword = {},
            )
        }
    }
}

// endregion
