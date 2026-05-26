package com.doannd3.treetask.feature.auth.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.component.LinkPart
import com.doannd3.treetask.core.designsystem.component.LinkTag
import com.doannd3.treetask.core.designsystem.component.LinkText
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.feature.auth.R

// region TreeTaskAppName

@Composable
internal fun TreeTaskAppName() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(com.doannd3.treetask.core.common.R.string.common_app_name),
        color = MaterialTheme.colorScheme.primary,
        fontSize = 50.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )
}

@AppPreviewLightDark
@Composable
private fun TreeTaskAppNamePreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            TreeTaskAppName()
        }
    }
}

// endregion

// region RegisterTextButton

@Composable
internal fun RegisterTextButton(onNavigateToRegister: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        val registerText = stringResource(R.string.auth_register)
        val text = stringResource(R.string.auth_prompt_no_account, registerText)

        LinkText(
            text = text,
            links = listOf(
                LinkPart(
                    text = registerText,
                    tag = LinkTag.REGISTER.name,
                    onClick = onNavigateToRegister,
                ),
            ),
            parentStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
            ),
            linkStyle = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
                textDecoration = TextDecoration.Underline,
            ),
        )
    }
}

@AppPreviewLightDark
@Composable
private fun RegisterTextButtonPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            RegisterTextButton(onNavigateToRegister = {})
        }
    }
}

// endregion

// region ForgotPasswordTextButton

@Composable
internal fun ForgotPasswordTextButton(onNavigateToForgotPassword: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        val forgotPassword = stringResource(R.string.auth_forgot_password)

        LinkText(
            text = forgotPassword,
            links = listOf(
                LinkPart(
                    text = forgotPassword,
                    tag = LinkTag.FORGOT_PASSWORD.name,
                    onClick = onNavigateToForgotPassword,
                ),
            ),
            parentStyle = TextStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
            ),
            linkStyle = SpanStyle(
                color = MaterialTheme.colorScheme.primary,
                fontSize = 15.sp,
                textDecoration = TextDecoration.Underline,
            ),
        )
    }
}

@AppPreviewLightDark
@Composable
private fun ForgotPasswordTextButtonPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ForgotPasswordTextButton(onNavigateToForgotPassword = {})
        }
    }
}

// endregion
