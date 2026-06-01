package com.doannd3.treetask.feature.auth.ui.register

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextDecoration
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.component.LinkPart
import com.doannd3.treetask.core.designsystem.component.LinkTag
import com.doannd3.treetask.core.designsystem.component.LinkText
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.feature.auth.R

// region AlreadyHaveAccountTextButton

@Composable
internal fun AlreadyHaveAccountTextButton(onRegisterBack: () -> Unit) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.Center,
    ) {
        val loginText = stringResource(R.string.auth_action_login)
        val text = stringResource(R.string.auth_prompt_already_have_account, loginText)

        LinkText(
            text = text,
            links =
                listOf(
                    LinkPart(
                        text = loginText,
                        tag = LinkTag.LOGIN.name,
                        onClick = onRegisterBack,
                    ),
                ),
            parentStyle =
                TextStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp,
                ),
            linkStyle =
                SpanStyle(
                    color = MaterialTheme.colorScheme.primary,
                    fontSize = 15.sp,
                    textDecoration = TextDecoration.Underline,
                ),
        )
    }
}

@AppPreviewLightDark
@Composable
private fun AlreadyHaveAccountTextButtonPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            AlreadyHaveAccountTextButton(onRegisterBack = {})
        }
    }
}

// endregion

// region FullNameInput

@Composable
internal fun FullNameInput(
    modifier: Modifier = Modifier,
    fullName: String,
    onFullNameChange: (String) -> Unit,
    onImeNext: () -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
        colors =
            OutlinedTextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
        textStyle = TextStyle(fontSize = 15.sp),
        singleLine = true,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Next,
            ),
        keyboardActions = KeyboardActions(onNext = { onImeNext() }),
        value = fullName,
        onValueChange = { onFullNameChange(it) },
        label = { Text(text = stringResource(R.string.auth_full_name_hint)) },
    )
}

@AppPreviewLightDark
@Composable
private fun FullNameInputPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FullNameInput(
                fullName = "Đoàn Nguyễn",
                onFullNameChange = {},
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun FullNameInputEmptyPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FullNameInput(
                fullName = "",
                onFullNameChange = {},
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun FullNameInputLongPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            FullNameInput(
                fullName = "Nguyễn Đoàn Minh Long",
                onFullNameChange = {},
            )
        }
    }
}

// endregion
