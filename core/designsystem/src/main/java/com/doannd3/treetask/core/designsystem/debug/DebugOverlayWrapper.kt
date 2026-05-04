package com.doannd3.treetask.core.designsystem.debug

import android.annotation.SuppressLint
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.ColorDev
import com.doannd3.treetask.core.designsystem.theme.ColorTextDev

@SuppressLint("UnusedBoxWithConstraintsScope")
@Composable
fun DebugOverlayWrapper(
    isVisible: Boolean,
    label: String,
    content: @Composable () -> Unit,
) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        content()

        if (isVisible) {
            val offsetY = maxHeight / 5
            Text(
                modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = offsetY)
                    .background(ColorDev)
                    .padding(horizontal = 10.dp),
                text = label,
                color = ColorTextDev,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}

@Preview(showBackground = true)
@Composable
fun DebugOverlayWrapperPreview() {
    DebugOverlayWrapper(
        isVisible = true,
        label = "PROD • DEBUG",
    ) {
        Box(
            modifier =
            Modifier
                .fillMaxSize()
                .background(Color.LightGray),
            contentAlignment = Alignment.Center,
        ) {
            Text(text = "Main Content")
        }
    }
}
