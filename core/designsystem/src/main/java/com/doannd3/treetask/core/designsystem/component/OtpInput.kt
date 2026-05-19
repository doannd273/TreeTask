package com.doannd3.treetask.core.designsystem.component

import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicTextField
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.graphics.SolidColor
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme

@Composable
fun OtpInput(
    value: String,
    onValueChange: (String) -> Unit,
    onOtpComplete: (String) -> Unit,
    modifier: Modifier = Modifier,
    otpLength: Int = DEFAULT_OTP_LENGTH,
    enabled: Boolean = true,
    isError: Boolean = false,
) {
    val resolvedOtpLength = otpLength.coerceAtLeast(MIN_OTP_LENGTH)
    val currentValue = value.filter(Char::isDigit).take(resolvedOtpLength)

    BasicTextField(
        modifier = modifier.fillMaxWidth(),
        value = currentValue,
        onValueChange = { input ->
            val normalizedValue = input.filter(Char::isDigit).take(resolvedOtpLength)
            if (normalizedValue == currentValue) {
                return@BasicTextField
            }

            onValueChange(normalizedValue)

            if (normalizedValue.length == resolvedOtpLength) {
                onOtpComplete(normalizedValue)
            }
        },
        enabled = enabled,
        singleLine = true,
        keyboardOptions = KeyboardOptions(keyboardType = KeyboardType.NumberPassword),
        cursorBrush = SolidColor(MaterialTheme.colorScheme.primary),
        decorationBox = { innerTextField ->
            Box {
                Row(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(8.dp),
                ) {
                    repeat(resolvedOtpLength) { index ->
                        OtpDigitBox(
                            modifier =
                            Modifier
                                .weight(1f)
                                .height(52.dp),
                            digit = currentValue.getOrNull(index)?.toString().orEmpty(),
                            isActive = currentValue.length == index,
                            enabled = enabled,
                            isError = isError,
                        )
                    }
                }

                Box(
                    modifier =
                    Modifier
                        .size(1.dp)
                        .alpha(0f),
                ) {
                    innerTextField()
                }
            }
        },
    )
}

@Composable
private fun OtpDigitBox(
    digit: String,
    isActive: Boolean,
    enabled: Boolean,
    isError: Boolean,
    modifier: Modifier = Modifier,
) {
    val borderColor =
        when {
            isError -> MaterialTheme.colorScheme.error
            isActive -> MaterialTheme.colorScheme.primary
            enabled -> MaterialTheme.colorScheme.outline
            else -> MaterialTheme.colorScheme.onSurfaceVariant
        }

    val textColor = if (enabled) MaterialTheme.colorScheme.onSurface else MaterialTheme.colorScheme.onSurfaceVariant

    Box(
        modifier =
        modifier
            .border(
                width = if (isActive) 2.dp else 1.dp,
                color = borderColor,
                shape = RoundedCornerShape(6.dp),
            ).padding(horizontal = 4.dp),
        contentAlignment = Alignment.Center,
    ) {
        Text(
            text = digit,
            color = textColor,
            fontSize = 20.sp,
            fontWeight = FontWeight.SemiBold,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.bodyLarge,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun OtpInputEmptyPreview() {
    TreeTaskTheme {
        OtpInput(
            modifier = Modifier.padding(16.dp),
            value = "",
            onValueChange = {},
            onOtpComplete = {},
        )
    }
}

@AppPreviewLightDark
@Composable
private fun OtpInputPartialPreview() {
    TreeTaskTheme {
        OtpInput(
            modifier = Modifier.padding(16.dp),
            value = "123",
            onValueChange = {},
            onOtpComplete = {},
        )
    }
}

@AppPreviewLightDark
@Composable
private fun OtpInputErrorPreview() {
    TreeTaskTheme {
        OtpInput(
            modifier = Modifier.padding(16.dp),
            value = "123456",
            onValueChange = {},
            onOtpComplete = {},
            isError = true,
        )
    }
}

private const val DEFAULT_OTP_LENGTH = 6
private const val MIN_OTP_LENGTH = 1
