package com.doannd3.treetask.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.R
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme

@Composable
fun CommonHeader(
    title: String,
    onNavigateBack: () -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .height(50.dp)
                .background(color = MaterialTheme.colorScheme.background)
                .padding(horizontal = 8.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        IconButton(onClick = onNavigateBack) {
            Icon(
                painter = painterResource(R.drawable.designsystem_ic_back_left),
                contentDescription = stringResource(R.string.designsystem_cd_navigate_back),
                tint = MaterialTheme.colorScheme.primary,
            )
        }

        Text(
            text = title,
            color = MaterialTheme.colorScheme.primary,
            style = TextStyle(fontSize = 18.sp),
        )
    }
}

@AppPreviewLightDark
@Composable
private fun CommonHeaderPreview() {
    TreeTaskTheme {
        CommonHeader(
            title = "Đăng ký",
            onNavigateBack = {},
        )
    }
}
