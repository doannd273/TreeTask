package com.doannd3.treetask.feature.auth.ui.forgotpassword

import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.doannd3.treetask.feature.auth.contract.ForgotPasswordState

@Composable
fun ForgotPasswordScreen(
    state: ForgotPasswordState,
    onEmailChange: (String) -> Unit,
    onSubmitForgotPassword: () -> Unit,
    onForgotPasswordBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        ForgotPasswordContent(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            state = state,
            onEmailChange = onEmailChange,
            onSubmitForgotPassword = onSubmitForgotPassword,
            onForgotPasswordBack = onForgotPasswordBack,
        )
    }
}

@Composable
@Preview(showBackground = true)
fun ForgotPasswordPreview() {
    ForgotPasswordScreen(
        state = ForgotPasswordState(
            email = "demo@gmail.com",
        ),
        onEmailChange = {},
        onSubmitForgotPassword = {},
        onForgotPasswordBack = {}
    )
}