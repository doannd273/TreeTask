package com.doannd3.treetask.feature.auth.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.KeyboardActions
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.OutlinedTextFieldDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.Black
import com.doannd3.treetask.core.designsystem.theme.Gray
import com.doannd3.treetask.core.designsystem.theme.Purple40
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.designsystem.theme.White
import com.doannd3.treetask.feature.auth.R

@Composable
fun FullNameInput(
    modifier: Modifier = Modifier,
    fullName: String,
    onFullNameChange: (String) -> Unit,
    onImeNext: () -> Unit = {},
) {
    OutlinedTextField(
        modifier = modifier.fillMaxWidth(),
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
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        keyboardActions =
        KeyboardActions(onNext = { onImeNext() }),
        value = fullName,
        onValueChange = { onFullNameChange(it) },
        label = { Text(text = stringResource(R.string.auth_full_name_hint)) },
    )
}

@Composable
fun RegisterHeader(onRegisterBack: () -> Unit) {
    Row(
        modifier =
        Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onRegisterBack) {
            Icon(
                painter = painterResource(R.drawable.auth_ic_back_left),
                contentDescription = stringResource(R.string.auth_cd_navigate_back),
                tint = Purple40,
            )
        }

        Text(
            text = stringResource(R.string.auth_register),
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
private fun FullNameInputPreview() {
    TreeTaskTheme {
        FullNameInput(
            fullName = "Đoàn Nguyễn",
            onFullNameChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullNameInputEmptyPreview() {
    TreeTaskTheme {
        FullNameInput(
            fullName = "",
            onFullNameChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun FullNameInputLongPreview() {
    TreeTaskTheme {
        FullNameInput(
            fullName = "Nguyễn Đoàn Minh Long",
            onFullNameChange = {},
        )
    }
}

@Preview(showBackground = true)
@Composable
private fun RegisterHeaderPreview() {
    TreeTaskTheme {
        RegisterHeader(
            onRegisterBack = {},
        )
    }
}
