package com.doannd3.treetask.feature.auth.ui.forgotpassword

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.Purple40
import com.doannd3.treetask.core.designsystem.theme.White
import com.doannd3.treetask.feature.auth.R

@Composable
internal fun ForgotPasswordHeader(onForgotPasswordBack: () -> Unit) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp)
            .background(color = White)
            .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onForgotPasswordBack) {
            Icon(
                painter = painterResource(R.drawable.auth_ic_back_left),
                contentDescription = null,
                tint = Purple40,
            )
        }

        Text(
            text = stringResource(R.string.auth_forgot_password),
            color = Purple40,
            style = TextStyle(
                fontSize = 18.sp,
            ),
        )
    }
}

@Composable
internal fun ForgotPasswordButton(
    onSubmitForgotPassword: () -> Unit,
) {
    Button(
        modifier = Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors = ButtonDefaults.buttonColors(
            containerColor = Purple40,
        ),
        shape = RoundedCornerShape(corner = CornerSize(size = 3.dp)),
        onClick = { onSubmitForgotPassword() },
    ) {
        Text(text = stringResource(R.string.auth_confirm), color = White, fontSize = 16.sp)
    }
}
