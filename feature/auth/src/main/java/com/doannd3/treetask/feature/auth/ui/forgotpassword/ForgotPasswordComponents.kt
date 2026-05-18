package com.doannd3.treetask.feature.auth.ui.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.component.CommonButton
import com.doannd3.treetask.core.designsystem.component.OtpInput
import com.doannd3.treetask.core.designsystem.theme.Purple40
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.designsystem.theme.White
import com.doannd3.treetask.core.designsystem.util.rememberDebouncedClick
import com.doannd3.treetask.feature.auth.R
import com.doannd3.treetask.feature.auth.ui.login.EmailInput
import com.doannd3.treetask.feature.auth.ui.login.PasswordInput

@Composable
fun EmailStep(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    state: ForgotPasswordState,
    onEvent: (ForgotPasswordEvent) -> Unit,
) {
    if (isVisible) {
        val onSubmitSendEmailDebounced =
            rememberDebouncedClick {
                onEvent(ForgotPasswordEvent.SubmitEmail)
            }

        Column(
            modifier = modifier.padding(16.dp),
        ) {
            EmailInput(
                modifier = Modifier.fillMaxWidth(),
                email = state.email,
                isEnable = state.step == ForgotPasswordStep.EmailInput,
                onEmailChange = { onEvent(ForgotPasswordEvent.EmailChanged(it)) },
                imeAction = ImeAction.Done,
                onImeDone = {
                    onSubmitSendEmailDebounced()
                },
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

@Composable
fun ResetPasswordStep(
    modifier: Modifier = Modifier,
    isVisible: Boolean = true,
    state: ForgotPasswordState,
    onEvent: (ForgotPasswordEvent) -> Unit,
) {
    if (isVisible) {
        val onSubmitResetPasswordDebounced =
            rememberDebouncedClick {
                onEvent(ForgotPasswordEvent.SubmitResetPassword)
            }
        val onResendOtpDebounced =
            rememberDebouncedClick {
                onEvent(ForgotPasswordEvent.ResendOtp)
            }

        val passwordFocusRequester = remember { FocusRequester() }

        Column(
            modifier = modifier.padding(16.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.auth_otp_sent_to_email, state.email),
                color = Purple40,
                fontSize = 14.sp,
            )

            Spacer(modifier = Modifier.height(16.dp))

            OtpInput(
                value = state.otp,
                onValueChange = { onEvent(ForgotPasswordEvent.OtpChanged(it)) },
                onOtpComplete = {
                    passwordFocusRequester.requestFocus()
                },
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
            )

            Spacer(modifier = Modifier.height(16.dp))

            PasswordInput(
                modifier =
                Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                password = state.newPassword,
                passwordVisible = state.passwordVisible,
                onPasswordChange = { onEvent(ForgotPasswordEvent.NewPasswordChanged(it)) },
                onPasswordVisibleChange = { onEvent(ForgotPasswordEvent.PasswordVisibleChanged(it)) },
                imeAction = ImeAction.Done,
                onImeDone = {
                    onSubmitResetPasswordDebounced()
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            TextButton(
                modifier = Modifier.fillMaxWidth(),
                enabled = !state.isLoading,
                onClick = onResendOtpDebounced,
            ) {
                Text(
                    text = stringResource(R.string.auth_resend_otp),
                    color = Purple40,
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

@Composable
fun ForgotPasswordHeader(onForgotPasswordBack: () -> Unit) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onForgotPasswordBack) {
            Icon(
                painter = painterResource(R.drawable.auth_ic_back_left),
                contentDescription = stringResource(R.string.auth_cd_navigate_back),
                tint = Purple40,
            )
        }

        Text(
            text = stringResource(R.string.auth_forgot_password),
            color = Purple40,
            style =
            TextStyle(
                fontSize = 18.sp,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailStepPreview() {
    TreeTaskTheme {
        EmailStep(
            state =
            ForgotPasswordState(
                email = "doan@gmail.com",
                step = ForgotPasswordStep.EmailInput,
                isLoading = false,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ForgotPasswordHeaderPreview() {
    TreeTaskTheme {
        ForgotPasswordHeader(
            onForgotPasswordBack = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun EmailStepLoadingPreview() {
    TreeTaskTheme {
        EmailStep(
            state =
            ForgotPasswordState(
                email = "doan@gmail.com",
                step = ForgotPasswordStep.EmailInput,
                isLoading = true,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResetPasswordStepPreview() {
    TreeTaskTheme {
        ResetPasswordStep(
            state =
            ForgotPasswordState(
                step = ForgotPasswordStep.ResetInput,
                email = "doan@gmail.com",
                otp = "123",
                newPassword = "",
                isLoading = false,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResetPasswordStepFilledPreview() {
    TreeTaskTheme {
        ResetPasswordStep(
            state =
            ForgotPasswordState(
                step = ForgotPasswordStep.ResetInput,
                email = "doan@gmail.com",
                otp = "123456",
                newPassword = "password123",
                passwordVisible = false,
                isLoading = false,
            ),
            onEvent = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun ResetPasswordStepLoadingPreview() {
    TreeTaskTheme {
        ResetPasswordStep(
            state =
            ForgotPasswordState(
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
