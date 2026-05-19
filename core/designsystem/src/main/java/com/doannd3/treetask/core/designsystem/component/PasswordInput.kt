package com.doannd3.treetask.core.designsystem.component

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
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.text.input.VisualTransformation
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.R
import com.doannd3.treetask.core.designsystem.theme.Black
import com.doannd3.treetask.core.designsystem.theme.Gray
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme

@Composable
fun PasswordInput(
    modifier: Modifier = Modifier,
    label: String,
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
        label = { Text(text = label) },
        trailingIcon = {
            IconButton(
                enabled = enabled,
                onClick = { onPasswordVisibleChange(!passwordVisible) },
            ) {
                val passwordVisibilityDescription =
                    stringResource(
                        if (passwordVisible) {
                            R.string.designsystem_cd_hide_password
                        } else {
                            R.string.designsystem_cd_show_password
                        },
                    )
                Icon(
                    painterResource(
                        if (passwordVisible) {
                            R.drawable.designsystem_ic_visibility
                        } else {
                            R.drawable.designsystem_ic_visibility_off
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

@Preview(showBackground = true)
@Composable
private fun PasswordInputEmptyPreview() {
    TreeTaskTheme {
        PasswordInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Password",
            password = "",
            passwordVisible = false,
            onPasswordChange = {},
            onPasswordVisibleChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordInputFilledHiddenPreview() {
    TreeTaskTheme {
        PasswordInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Password",
            password = "secret123",
            passwordVisible = false,
            onPasswordChange = {},
            onPasswordVisibleChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordInputFilledVisiblePreview() {
    TreeTaskTheme {
        PasswordInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Password",
            password = "secret123",
            passwordVisible = true,
            onPasswordChange = {},
            onPasswordVisibleChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun PasswordInputDisabledPreview() {
    TreeTaskTheme {
        PasswordInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Password",
            password = "secret123",
            passwordVisible = false,
            enabled = false,
            onPasswordChange = {},
            onPasswordVisibleChange = {},
        )
    }
}
