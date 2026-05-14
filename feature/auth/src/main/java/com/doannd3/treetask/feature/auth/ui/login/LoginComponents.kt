package com.doannd3.treetask.feature.auth.ui.login

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.component.LinkPart
import com.doannd3.treetask.core.designsystem.component.LinkTag
import com.doannd3.treetask.core.designsystem.component.LinkText
import com.doannd3.treetask.core.designsystem.theme.Black
import com.doannd3.treetask.core.designsystem.theme.Gray
import com.doannd3.treetask.core.designsystem.theme.Purple40
import com.doannd3.treetask.core.designsystem.theme.White
import com.doannd3.treetask.feature.auth.R

@Composable
internal fun TreeTaskAppName() {
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
internal fun PasswordInput(
    modifier: Modifier,
    password: String,
    passwordVisible: Boolean,
    onPasswordChange: (String) -> Unit,
    onPasswordVisibleChange: (Boolean) -> Unit,
    onImeDone: () -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier,
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
                imeAction = ImeAction.Done,
            ),
        keyboardActions =
            KeyboardActions(onDone = { onImeDone() }),
        value = password,
        onValueChange = { onPasswordChange(it) },
        label = { Text(text = stringResource(R.string.auth_password_hint)) },
        trailingIcon = {
            IconButton(onClick = { onPasswordVisibleChange(!passwordVisible) }) {
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
internal fun EmailInput(
    modifier: Modifier,
    email: String,
    onEmailChange: (String) -> Unit,
    imeAction: ImeAction = ImeAction.Next,
    onImeNext: () -> Unit = {},
    onImeDone: () -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier,
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
internal fun LoginButton(
    isEnable: Boolean,
    onSubmitLogin: () -> Unit,
) {
    Button(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(50.dp),
        colors =
            ButtonDefaults.buttonColors(
                containerColor = Purple40,
            ),
        shape = RoundedCornerShape(corner = CornerSize(size = 3.dp)),
        enabled = isEnable,
        onClick = { onSubmitLogin() },
    ) {
        Text(text = stringResource(R.string.auth_action_login), color = White, fontSize = 16.sp)
    }
}

@Composable
internal fun RegisterTextButton(onNavigateToRegister: () -> Unit) {
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
internal fun ForgotPasswordTextButton(onNavigateToForgotPassword: () -> Unit) {
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
