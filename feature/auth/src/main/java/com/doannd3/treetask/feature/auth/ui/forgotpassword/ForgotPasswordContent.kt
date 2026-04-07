package com.doannd3.treetask.feature.auth.ui.forgotpassword

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.feature.auth.contract.ForgotPasswordState
import com.doannd3.treetask.feature.auth.ui.login.EmailInput

@Composable
fun ForgotPasswordContent(
    modifier: Modifier = Modifier,
    state: ForgotPasswordState,
    onEmailChange: (String) -> Unit,
    onSubmitForgotPassword: () -> Unit,
    onForgotPasswordBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        ForgotPasswordHeader(onForgotPasswordBack = onForgotPasswordBack)

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            EmailInput(
                email = state.email,
                onEmailChange = onEmailChange
            )

            Spacer(modifier = Modifier.height(16.dp))

            ForgotPasswordButton(
                onSubmitForgotPassword = onSubmitForgotPassword
            )
        }
    }
}