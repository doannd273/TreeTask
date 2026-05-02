package com.doannd3.treetask.core.designsystem.component

import androidx.compose.material3.AlertDialog
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.tooling.preview.Preview
import com.doannd3.treetask.core.designsystem.R

@Composable
fun AppErrorDialog(
    errorMessage: String?,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    if (errorMessage == null) return
    AlertDialog(
        modifier = modifier,
        onDismissRequest = onDismiss,
        title = {
            Text(text = stringResource(R.string.designsystem_error_dialog_title))
        },
        text = {
            Text(text = errorMessage)
        },
        confirmButton = {
            TextButton(onClick = onDismiss) {
                Text(stringResource(R.string.designsystem_action_close))
            }
        },
    )
}

@Composable
@Preview(showBackground = true)
fun AppErrorDialogPreview() {
    AppErrorDialog(errorMessage = "Có lỗi", onDismiss = {})
}
