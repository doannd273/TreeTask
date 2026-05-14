package com.doannd3.treetask.core.designsystem.component

import androidx.compose.foundation.clickable
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
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.tooling.preview.datasource.LoremIpsum
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import com.doannd3.treetask.core.designsystem.R
import com.doannd3.treetask.core.designsystem.theme.DividerPrimary
import com.doannd3.treetask.core.designsystem.theme.TextPrimary
import com.doannd3.treetask.core.designsystem.theme.TextWarning

@Composable
fun AppErrorDialog(
    errorMessage: String?,
    modifier: Modifier = Modifier,
    onDismiss: () -> Unit,
) {
    if (errorMessage == null) return

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
                        text = stringResource(R.string.designsystem_error_dialog_title),
                        color = TextPrimary,
                        fontSize = 18.sp,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )

                    Spacer(modifier = Modifier.height(8.dp))

                    Text(
                        text = errorMessage,
                        color = TextPrimary,
                        fontSize = 14.sp,
                        textAlign = TextAlign.Center,
                    )
                }

                HorizontalDivider(thickness = 0.3.dp, color = DividerPrimary)

                Text(
                    modifier =
                        Modifier
                            .fillMaxWidth()
                            .clickable(onClick = onDismiss)
                            .padding(vertical = 12.dp),
                    text = stringResource(R.string.designsystem_action_ok),
                    fontSize = 16.sp,
                    color = TextWarning,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )
            }
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppErrorDialogPreview() {
    AppErrorDialog(errorMessage = LoremIpsum(40).values.joinToString(), onDismiss = {})
}
