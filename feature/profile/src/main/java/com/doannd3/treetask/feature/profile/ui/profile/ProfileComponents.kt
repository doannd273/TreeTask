package com.doannd3.treetask.feature.profile.ui.profile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonColors
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.BackgroundPrimary
import com.doannd3.treetask.core.designsystem.theme.Purple40
import com.doannd3.treetask.core.designsystem.theme.TextPrimary
import com.doannd3.treetask.core.designsystem.theme.TextSecondary
import com.doannd3.treetask.feature.profile.R

@Composable
fun LogoutButton(
    isEnable: Boolean,
    onSubmitLogout: () -> Unit,
) {
    Button(
        modifier =
            Modifier.fillMaxWidth()
                .height(50.dp),
        colors = ButtonDefaults.buttonColors(
                containerColor = BackgroundPrimary,
            ),
        enabled = isEnable,
        onClick = onSubmitLogout,
    ) {
        Text(
            text = stringResource(R.string.profile_action_logout),
            color = TextSecondary,
            fontSize = 16.sp,
        )
    }
}

@Preview
@Composable
fun LogoutButtonPreview() {
    LogoutButton(
        isEnable = true,
        {},
    )
}
