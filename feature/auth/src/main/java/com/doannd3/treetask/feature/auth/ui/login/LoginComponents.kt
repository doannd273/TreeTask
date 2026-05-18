package com.doannd3.treetask.feature.auth.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.component.LinkPart
import com.doannd3.treetask.core.designsystem.component.LinkTag
import com.doannd3.treetask.core.designsystem.component.LinkText
import com.doannd3.treetask.core.designsystem.theme.Black
import com.doannd3.treetask.core.designsystem.theme.Gray
import com.doannd3.treetask.core.designsystem.theme.Purple40
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.feature.auth.R

@Composable
fun TreeTaskAppName() {
    Text(
        modifier = Modifier.fillMaxWidth(),
        text = stringResource(com.doannd3.treetask.core.common.R.string.common_app_name),
        color = Purple40,
        fontSize = 50.sp,
        fontWeight = FontWeight.Bold,
        textAlign = TextAlign.Center,
    )
}

@Composable
fun PasswordInput(
    modifier: Modifier = Modifier,
    password: String,
    passwordVisible: Boolean,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    enabled: Boolean = true,
    imeAction: ImeAction = ImeAction.Done,
    onImeNext: () -> Unit = {},
    onImeDone: () -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier,
        enabled = enabled,
        colors =
        OutlinedTextFieldDefaults.colors(
            focusedTextColor = Black,
            unfocusedTextColor = Gray,
        ),
        textStyle =
        TextStyle(
            fontSize = 15.sp,
        ),
        singleLine = true,
        keyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Password,
            imeAction = imeAction,
        ),
        keyboardActions =
        KeyboardActions(
            onNext = { onImeNext() },
            onDone = { onImeDone() },
        ),
        value = password,
        onValueChange = { onPasswordChange(it) },
        label = { Text(text = stringResource(R.string.auth_password_hint)) },
        trailingIcon = {
            IconButton(
                enabled = enabled,
                onClick = { onPasswordVisibleChange(!passwordVisible) },
            ) {
                val passwordVisibilityDescription =
                    stringResource(
                        if (passwordVisible) {
                            R.string.auth_cd_hide_password
                        } else {
                            R.string.auth_cd_show_password
                        },
                    )
                Icon(
                    painterResource(
                        if (passwordVisible) {
                            R.drawable.auth_ic_visibility
                        } else {
                            R.drawable.auth_ic_visibility_off
                        },
                    ),
                    contentDescription = passwordVisibilityDescription,
                )
            }
        },
        visualTransformation =
        if (passwordVisible) {
            VisualTransformation.None
        } else {
            PasswordVisualTransformation()
        },
    )
}

@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    email: String,
    isEnable: Boolean = true,
    onEmailChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Next,
    onImeNext: () -> Unit = {},
    onImeDone: () -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier,
        enabled = isEnable,
        colors =
        OutlinedTextFieldDefaults.colors(
            focusedTextColor = Black,
            unfocusedTextColor = Gray,
        ),
        textStyle =
        TextStyle(
            fontSize = 15.sp,
        ),
        singleLine = true,
        keyboardOptions =
        KeyboardOptions(
            keyboardType = KeyboardType.Email,
            imeAction = imeAction,
        ),
        keyboardActions =
        KeyboardActions(
            onNext = { onImeNext() },
            onDone = { onImeDone() },
        ),
        value = email,
        onValueChange = { onEmailChange(it) },
        label = { Text(text = stringResource(R.string.auth_email_hint)) },
    )
}

@Composable
fun RegisterTextButton(onNavigateToRegister: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        val registerText = stringResource(R.string.auth_register)
        val text =
            stringResource(
                R.string.auth_prompt_no_account,
                registerText,
            )

        LinkText(
            text = text,
            links =
            listOf(
                LinkPart(
                    text = registerText,
                    tag = LinkTag.REGISTER.name,
                    onClick = onNavigateToRegister,
                ),
            ),
            parentStyle =
            TextStyle(
                color = Purple40,
                fontSize = 15.sp,
            ),
            linkStyle =
            SpanStyle(
                color = Purple40,
                fontSize = 15.sp,
                textDecoration = TextDecoration.Underline,
            ),
        )
    }
}

@Composable
fun ForgotPasswordTextButton(onNavigateToForgotPassword: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.End,
    ) {
        val forgotPassword = stringResource(R.string.auth_forgot_password)

        LinkText(
            text = forgotPassword,
            links =
            listOf(
                LinkPart(
                    text = forgotPassword,
                    tag = LinkTag.FORGOT_PASSWORD.name,
                    onClick = onNavigateToForgotPassword,
                ),
            ),
            parentStyle =
            TextStyle(
                color = Purple40,
                fontSize = 15.sp,
            ),
            linkStyle =
            SpanStyle(
                color = Purple40,
                fontSize = 15.sp,
                textDecoration = TextDecoration.Underline,
            ),
        )
    }
}

@Preview(showBackground = true)
@Composable
fun TreeTaskAppNamePreview() {
    TreeTaskTheme {
        TreeTaskAppName()
    }
}

@Preview(showBackground = true)
@Composable
fun EmailInputPreview() {
    TreeTaskTheme {
        EmailInput(
            modifier = Modifier.fillMaxWidth(),
            email = "doan@gmail.com",
            onEmailChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmailInputEmptyPreview() {
    TreeTaskTheme {
        EmailInput(
            email = "",
            onEmailChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun EmailInputDisabledPreview() {
    TreeTaskTheme {
        EmailInput(
            email = "doan@gmail.com",
            isEnable = false,
            onEmailChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordInputHiddenPreview() {
    TreeTaskTheme {
        PasswordInput(
            modifier = Modifier.fillMaxWidth(),
            password = "123456",
            passwordVisible = false,
            onPasswordChange = {},
            onPasswordVisibleChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordInputVisiblePreview() {
    TreeTaskTheme {
        PasswordInput(
            modifier = Modifier.fillMaxWidth(),
            password = "123456",
            passwordVisible = true,
            onPasswordChange = {},
            onPasswordVisibleChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun PasswordInputDisabledPreview() {
    TreeTaskTheme {
        PasswordInput(
            password = "123456",
            passwordVisible = false,
            enabled = false,
            onPasswordChange = {},
            onPasswordVisibleChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun RegisterTextButtonPreview() {
    TreeTaskTheme {
        RegisterTextButton(
            onNavigateToRegister = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
fun ForgotPasswordTextButtonPreview() {
    TreeTaskTheme {
        ForgotPasswordTextButton(
            onNavigateToForgotPassword = {},
        )
    }
}
