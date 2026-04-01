package com.doannd3.treetask.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.window.Dialog
import androidx.compose.ui.window.DialogProperties

@Composable
fun AppLoadingDialog(
    isLoading: Boolean,
    modifier: Modifier = Modifier // component dùng chung bao giờ cũng phải có param modifier
) {
    if (!isLoading) return // Nếu không loading thì ẩn
    Dialog(
        onDismissRequest = { /* Không làm gì cả để chặn user tắt loading giữa chừng */ },
        properties = DialogProperties(
            dismissOnBackPress = false, // Khoá nút Back
            dismissOnClickOutside = false // Khoá bấm ra ngoài
        )
    ) {
        Box(
            modifier = modifier
                .size(100.dp)
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(16.dp)
                ),
            contentAlignment = Alignment.Center
        ) {
            CircularProgressIndicator()
        }
    }
}

@Composable
@Preview(showBackground = true)
fun AppLoadingDialogPreview() {
    AppLoadingDialog(isLoading = true)
}