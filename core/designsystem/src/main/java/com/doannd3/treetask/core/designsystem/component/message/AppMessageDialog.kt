package com.doannd3.treetask.core.designsystem.component.message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.doannd3.treetask.core.designsystem.R
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme

@Composable
fun AppMessageDialog(
    modifier: Modifier = Modifier,
    appDialogState: AppDialogState?,
    onDismiss: () -> Unit,
) {
    if (appDialogState == null) return

    if (appDialogState.message == null) return

    val title =
        appDialogState.title ?: when (appDialogState.type) {
            AppDialogType.Error -> stringResource(R.string.designsystem_error_dialog_title)
            AppDialogType.Success -> stringResource(R.string.designsystem_success_dialog_title)
            AppDialogType.Info -> stringResource(R.string.designsystem_info_dialog_title)
        }

    val scrollState = rememberScrollState()

    Dialog(onDismissRequest = onDismiss) {
        Surface(
            modifier =
            modifier
                .fillMaxWidth()
                .padding(horizontal = 24.dp, vertical = 8.dp),
            shape = RoundedCornerShape(20.dp),
            color = MaterialTheme.colorScheme.surface,
            tonalElevation = 8.dp,
        ) {
            Column(horizontalAlignment = Alignment.CenterHorizontally) {
                Column(
                    modifier =
                    Modifier
                        .padding(16.dp)
                        .heightIn(max = 400.dp)
                        .verticalScroll(scrollState),
                    horizontalAlignment = Alignment.CenterHorizontally,
                ) {
                    Text(
                        text =
                        title,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = appDialogState.message,
                        color = MaterialTheme.colorScheme.onSurface,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }

                HorizontalDivider(thickness = 0.3.dp, color = MaterialTheme.colorScheme.outlineVariant)

                TextButton(
                    modifier = Modifier.fillMaxWidth(),
                    onClick = { onDismiss() },
                ) {
                    Text(
                        text = stringResource(R.string.designsystem_action_ok),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }
            }
        }
    }
}

@AppPreviewLightDark
@Composable
private fun AppErrorDialogPreview() {
    TreeTaskTheme {
        AppMessageDialog(
            appDialogState =
            AppDialogState(
                title = "Thông báo lỗi",
                message = "Đăng nhập thất bại. Vui lòng kiểm tra lại email hoặc mật khẩu.",
                type = AppDialogType.Error,
            ),
            onDismiss = {},
        )
    }
}
