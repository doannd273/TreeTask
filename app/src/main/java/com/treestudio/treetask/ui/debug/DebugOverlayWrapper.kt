package com.treestudio.treetask.ui.debug

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.ColorDev
import com.doannd3.treetask.core.designsystem.theme.ColorTextDev
import com.treestudio.treetask.BuildConfig

@Composable
fun DebugOverlayWrapper(content: @Composable () -> Unit) {
    BoxWithConstraints(modifier = Modifier.fillMaxSize()) {
        content()

        if (BuildConfig.DEBUG) {
            val offsetY = maxHeight / 5
            Text(
                modifier =
                Modifier
                    .align(Alignment.TopEnd)
                    .offset(y = offsetY)
                    .background(ColorDev)
                    .padding(horizontal = 10.dp),
                text = "DEV",
                color = ColorTextDev,
                fontSize = 13.sp,
                fontWeight = FontWeight.Bold,
            )
        }
    }
}
