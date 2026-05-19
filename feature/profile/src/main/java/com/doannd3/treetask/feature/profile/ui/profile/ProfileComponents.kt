package com.doannd3.treetask.feature.profile.ui.profile

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
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
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        enabled = isEnable,
        onClick = onSubmitLogout,
    ) {
        Text(
            text = stringResource(R.string.profile_action_logout),
            color = MaterialTheme.colorScheme.onPrimary,
            fontSize = 16.sp,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun LogoutButtonPreview() {
    TreeTaskTheme {
        LogoutButton(
            isEnable = true,
            {},
        )
    }
}
