package com.doannd3.treetask.core.designsystem.component

import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.shape.CornerSize
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme

@Composable
fun CommonButton(
    buttonText: String,
    isEnable: Boolean,
    onSubmit: () -> Unit,
) {
    Button(
        modifier =
        Modifier
            .fillMaxWidth()
            .height(50.dp),
        colors =
        ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.primary,
        ),
        shape = RoundedCornerShape(corner = CornerSize(size = 3.dp)),
        enabled = isEnable,
        onClick = { onSubmit() },
    ) {
        Text(text = buttonText, color = MaterialTheme.colorScheme.onPrimary, fontSize = 16.sp)
    }
}

@AppPreviewLightDark
@Composable
private fun CommonButtonEnabledPreview() {
    TreeTaskTheme {
        CommonButton(
            buttonText = "Confirm",
            isEnable = true,
            onSubmit = {},
        )
    }
}

@AppPreviewLightDark
@Composable
private fun CommonButtonDisabledPreview() {
    TreeTaskTheme {
        CommonButton(
            buttonText = "Confirm",
            isEnable = false,
            onSubmit = {},
        )
    }
}
