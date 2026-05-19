package com.doannd3.treetask.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme

@Composable
fun EmailInput(
    modifier: Modifier = Modifier,
    label: String,
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
            focusedTextColor = MaterialTheme.colorScheme.onSurface,
            unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
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
        label = { Text(text = label) },
    )
}

@AppPreviewLightDark
@Composable
private fun EmailInputEmptyPreview() {
    TreeTaskTheme {
        EmailInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Email",
            email = "",
            onEmailChange = {},
        )
    }
}

@AppPreviewLightDark
@Composable
private fun EmailInputFilledPreview() {
    TreeTaskTheme {
        EmailInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Email",
            email = "demo@gmail.com",
            onEmailChange = {},
        )
    }
}

@AppPreviewLightDark
@Composable
private fun EmailInputDisabledPreview() {
    TreeTaskTheme {
        EmailInput(
            modifier = Modifier.fillMaxWidth(),
            label = "Email",
            email = "demo@gmail.com",
            isEnable = false,
            onEmailChange = {},
        )
    }
}
