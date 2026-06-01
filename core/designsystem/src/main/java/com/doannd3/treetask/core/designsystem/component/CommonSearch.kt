package com.doannd3.treetask.core.designsystem.component

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.KeyboardOptions
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.material3.TextField
import androidx.compose.material3.TextFieldDefaults
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.text.input.KeyboardType
import androidx.compose.ui.unit.dp
import com.doannd3.treetask.core.designsystem.R
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme

@Composable
fun CommonSearch(
    hintText: Int,
    isLoadingSearch: Boolean,
    searchQuery: String,
    onSearchChange: (String) -> Unit,
    onClearClick: () -> Unit,
) {
    TextField(
        modifier =
            Modifier
                .fillMaxWidth()
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.primary,
                    shape = RoundedCornerShape(6.dp),
                )
                .background(
                    color = MaterialTheme.colorScheme.surface,
                    shape = RoundedCornerShape(6.dp),
                ),
        colors =
            TextFieldDefaults.colors(
                focusedTextColor = MaterialTheme.colorScheme.onSurface,
                unfocusedTextColor = MaterialTheme.colorScheme.onSurfaceVariant,
                focusedIndicatorColor = Color.Transparent,
                unfocusedIndicatorColor = Color.Transparent,
                disabledIndicatorColor = Color.Transparent,
                focusedContainerColor = Color.Transparent,
                unfocusedContainerColor = Color.Transparent,
                disabledContainerColor = Color.Transparent,
            ),
        textStyle =
            MaterialTheme.typography.bodyMedium,
        singleLine = true,
        keyboardOptions =
            KeyboardOptions(
                keyboardType = KeyboardType.Text,
                imeAction = ImeAction.Done,
            ),
        placeholder = {
            Text(text = stringResource(hintText))
        },
        leadingIcon = {
            Icon(
                painterResource(R.drawable.designsystem_ic_search),
                contentDescription = stringResource(R.string.designsystem_cd_search),
            )
        },
        trailingIcon = {
            when {
                isLoadingSearch -> {
                    CircularProgressIndicator(
                        modifier = Modifier.size(18.dp),
                        strokeWidth = 2.dp,
                        color = MaterialTheme.colorScheme.primary,
                    )
                }

                searchQuery.isNotEmpty() -> {
                    IconButton(onClick = onClearClick) {
                        Icon(
                            painterResource(R.drawable.designsystem_ic_clear),
                            contentDescription = stringResource(R.string.designsystem_cd_clear_search),
                        )
                    }
                }
            }
        },
        value = searchQuery,
        onValueChange = onSearchChange,
    )
}

@AppPreviewLightDark
@Composable
private fun SearchTaskInputPreview() {
    TreeTaskTheme {
        CommonSearch(
            hintText = R.string.designsystem_cd_clear_search,
            searchQuery = "",
            isLoadingSearch = true,
            onSearchChange = {},
            onClearClick = {},
        )
    }
}
