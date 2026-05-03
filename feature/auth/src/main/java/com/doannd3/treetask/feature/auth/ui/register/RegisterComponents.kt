package com.doannd3.treetask.feature.auth.ui.register

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
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
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.Black
import com.doannd3.treetask.core.designsystem.theme.Gray
import com.doannd3.treetask.core.designsystem.theme.Purple40
import com.doannd3.treetask.core.designsystem.theme.White
import com.doannd3.treetask.feature.auth.R

@Composable
internal fun RegisterButton(
    onSubmitRegister: () -> Unit,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Purple40,
        ),
        shape = RoundedCornerShape(corner = CornerSize(size = 3.dp)),
        onClick = { onSubmitRegister() },
    ) {
        Text(text = stringResource(R.string.auth_register), color = White, fontSize = 16.sp)
    }
}

@Composable
internal fun FullNameInput(
    fullName: String,
    onFullNameChange: (String) -> Unit,
) {
    OutlinedTextField(
        modifier = Modifier.fillMaxWidth(),
        colors = OutlinedTextFieldDefaults.colors(
            focusedTextColor = Black,
            unfocusedTextColor = Gray,
        ),
        textStyle = TextStyle(
            fontSize = 15.sp,
        ),
        maxLines = 1,
        keyboardOptions = KeyboardOptions(
            keyboardType = KeyboardType.Text,
            imeAction = ImeAction.Next,
        ),
        value = fullName,
        onValueChange = { onFullNameChange(it) },
        label = { Text(text = stringResource(R.string.auth_full_name_hint)) },
    )
}

@Composable
internal fun RegisterHeader(onRegisterBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onRegisterBack) {
            Icon(
                painter = painterResource(R.drawable.auth_ic_back_left),
                contentDescription = null,
                tint = Purple40,
            )
        }

        Text(
            text = stringResource(R.string.auth_register),
            color = Purple40,
            style = TextStyle(
                fontSize = 18.sp,
            ),
        )
    }
}
