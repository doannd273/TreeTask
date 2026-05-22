package com.doannd3.treetask.feature.profile.ui.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.selection.selectable
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.window.Dialog
import coil.compose.AsyncImage
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.model.profile.AppLanguage
import com.doannd3.treetask.feature.profile.R

// region ProfileHeader

@Composable
fun ProfileHeader(
    modifier: Modifier = Modifier,
    avatarUrl: String?,
    fullName: String,
    email: String,
    phone: String?,
) {
    Column(
        modifier = modifier.fillMaxWidth(),
        horizontalAlignment = Alignment.CenterHorizontally,
    ) {
        AsyncImage(
            model = avatarUrl,
            contentDescription = stringResource(R.string.profile_cd_user_avatar),
            contentScale = ContentScale.Crop,
            modifier = Modifier
                .size(72.dp)
                .clip(CircleShape),
            placeholder = painterResource(R.drawable.profile_ic_avatar),
            error = painterResource(R.drawable.profile_ic_avatar),
        )

        Spacer(Modifier.height(12.dp))

        Text(
            text = fullName,
            style = MaterialTheme.typography.titleLarge,
            fontWeight = FontWeight.SemiBold,
        )
        Text(
            text = email,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
        )
        if (!phone.isNullOrBlank()) {
            Text(
                text = phone,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun ProfileHeaderPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileHeader(
                modifier = Modifier.padding(16.dp),
                avatarUrl = null,
                fullName = stringResource(R.string.profile_preview_full_name),
                email = stringResource(R.string.profile_preview_email),
                phone = stringResource(R.string.profile_preview_phone),
            )
        }
    }
}

// endregion

// region ProfileSwitchItem

@Composable
fun ProfileSwitchItem(
    icon: String,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier = Modifier
            .fillMaxWidth()
            .toggleable(
                value = checked,
                onValueChange = onCheckedChange,
                role = Role.Switch,
            )
            .padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = icon, fontSize = 20.sp)
        Spacer(Modifier.width(16.dp))
        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
        )
        Switch(
            checked = checked,
            onCheckedChange = null,
        )
    }
}

@AppPreviewLightDark
@Composable
private fun ProfileSwitchItemPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileSwitchItem(
                    icon = stringResource(R.string.profile_icon_dark_mode),
                    title = stringResource(R.string.profile_menu_dark_mode),
                    checked = true,
                    onCheckedChange = {},
                )
                HorizontalDivider()
                ProfileSwitchItem(
                    icon = stringResource(R.string.profile_icon_dark_mode),
                    title = stringResource(R.string.profile_menu_dark_mode),
                    checked = false,
                    onCheckedChange = {},
                )
            }
        }
    }
}

// endregion

// region ProfileItem

@Composable
fun ProfileItem(
    icon: String,
    title: String,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    trailing: @Composable (() -> Unit)? = null,
) {
    val contentAlpha = if (enabled) 1f else 0.38f

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .then(
                if (onClick != null && enabled) {
                    Modifier.clickable(onClick = onClick)
                } else {
                    Modifier
                },
            )
            .padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Text(text = icon, fontSize = 20.sp)

        Spacer(Modifier.width(16.dp))

        Text(
            text = title,
            style = MaterialTheme.typography.bodyLarge,
            modifier = Modifier.weight(1f),
            color = MaterialTheme.colorScheme.onSurface.copy(alpha = contentAlpha),
        )

        if (trailing != null) {
            trailing()
        } else if (onClick != null && enabled) {
            Icon(
                imageVector = Icons.AutoMirrored.Default.KeyboardArrowRight,
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun ProfileItemPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            Column(modifier = Modifier.fillMaxWidth()) {
                ProfileItem(
                    icon = stringResource(R.string.profile_icon_edit_profile),
                    title = stringResource(R.string.profile_menu_edit_profile),
                    onClick = {},
                )
                HorizontalDivider()
                ProfileItem(
                    icon = stringResource(R.string.profile_icon_language),
                    title = stringResource(R.string.profile_menu_language),
                    trailing = {
                        Text(
                            text = stringResource(R.string.profile_language_en),
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurfaceVariant,
                        )
                    },
                )
            }
        }
    }
}

// endregion

// region ProfileSection

@Composable
fun ProfileSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit,
) {
    Column(
        modifier = Modifier.fillMaxWidth(),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
            Spacer(Modifier.width(8.dp))
            HorizontalDivider(modifier = Modifier.weight(1f))
        }

        Spacer(Modifier.height(8.dp))

        Card(
            modifier = Modifier.fillMaxWidth(),
            shape = RoundedCornerShape(12.dp),
            colors = CardDefaults.cardColors(
                containerColor = MaterialTheme.colorScheme.surfaceContainerLow,
            ),
        ) {
            Column {
                content()
            }
        }
    }
}

@AppPreviewLightDark
@Composable
private fun ProfileSectionPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            ProfileSection(title = stringResource(R.string.profile_section_account)) {
                ProfileItem(
                    icon = stringResource(R.string.profile_icon_edit_profile),
                    title = stringResource(R.string.profile_menu_edit_profile),
                    onClick = {},
                )
                HorizontalDivider()
                ProfileItem(
                    icon = stringResource(R.string.profile_icon_change_password),
                    title = stringResource(R.string.profile_menu_change_password),
                    onClick = {},
                )
            }
        }
    }
}

// endregion

// region LogoutButton

@Composable
fun LogoutButton(
    isEnable: Boolean,
    onSubmitLogout: () -> Unit,
) {
    Button(
        onClick = onSubmitLogout,
        enabled = isEnable,
        modifier = Modifier.fillMaxWidth(),
        colors = ButtonDefaults.buttonColors(
            containerColor = MaterialTheme.colorScheme.errorContainer,
            contentColor = MaterialTheme.colorScheme.onErrorContainer,
        ),
        shape = RoundedCornerShape(12.dp),
    ) {
        Text(
            text = stringResource(R.string.profile_action_logout),
            style = MaterialTheme.typography.labelLarge,
            modifier = Modifier.padding(vertical = 4.dp),
        )
    }
}

@AppPreviewLightDark
@Composable
private fun LogoutButtonPreview() {
    TreeTaskTheme {
        Surface(
            modifier = Modifier
                .fillMaxWidth()
                .padding(16.dp),
            color = MaterialTheme.colorScheme.background,
        ) {
            LogoutButton(
                isEnable = true,
                onSubmitLogout = {},
            )
        }
    }
}

// endregion

// region LanguagePickerDialog

@Composable
fun LanguagePickerDialog(
    modifier: Modifier,
    currentLanguage: AppLanguage,
    onConfirm: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    Dialog(onDismissRequest = onDismiss) {
        LanguagePickerDialogContent(
            modifier = modifier,
            currentLanguage = currentLanguage,
            onConfirm = onConfirm,
            onDismiss = onDismiss,
        )
    }
}

internal fun AppLanguage.displayNameResId(): Int =
    when (this) {
        AppLanguage.ENGLISH -> R.string.profile_language_en
        AppLanguage.VIETNAMESE -> R.string.profile_language_vi
    }

@Composable
private fun LanguagePickerDialogContent(
    modifier: Modifier,
    currentLanguage: AppLanguage,
    onConfirm: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    val scrollState = rememberScrollState()
    var selectedLanguage by remember(currentLanguage) {
        mutableStateOf(currentLanguage)
    }

    Surface(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 24.dp, vertical = 8.dp),
        shape = RoundedCornerShape(20.dp),
        color = MaterialTheme.colorScheme.surface,
        tonalElevation = 8.dp,
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Column(
                modifier = Modifier
                    .padding(16.dp)
                    .heightIn(max = 400.dp)
                    .verticalScroll(scrollState),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                Text(
                    text = stringResource(R.string.profile_dialog_language_title),
                    color = MaterialTheme.colorScheme.onSurface,
                    fontSize = 18.sp,
                    fontWeight = FontWeight.Medium,
                    textAlign = TextAlign.Center,
                )

                Spacer(modifier = Modifier.height(8.dp))

                AppLanguage.entries.forEach { language ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .selectable(
                                selected = selectedLanguage == language,
                                onClick = { selectedLanguage = language },
                                role = Role.RadioButton,
                            )
                            .padding(4.dp),
                        verticalAlignment = Alignment.CenterVertically,
                    ) {
                        RadioButton(
                            selected = language == selectedLanguage,
                            onClick = null,
                        )
                        Spacer(Modifier.width(12.dp))
                        Text(
                            text = stringResource(language.displayNameResId()),
                            style = MaterialTheme.typography.bodyLarge,
                        )
                    }
                }
            }

            HorizontalDivider(
                thickness = 0.3.dp,
                color = MaterialTheme.colorScheme.outline,
            )

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .height(IntrinsicSize.Min),
            ) {
                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = onDismiss,
                ) {
                    Text(
                        text = stringResource(R.string.profile_action_cancel),
                        fontSize = 16.sp,
                        color = MaterialTheme.colorScheme.primary,
                        fontWeight = FontWeight.Medium,
                        textAlign = TextAlign.Center,
                    )
                }

                VerticalDivider(
                    modifier = Modifier.fillMaxHeight(),
                    thickness = 0.3.dp,
                    color = MaterialTheme.colorScheme.outline,
                )

                TextButton(
                    modifier = Modifier.weight(1f),
                    onClick = { onConfirm(selectedLanguage) },
                ) {
                    Text(
                        text = stringResource(R.string.profile_action_confirm),
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
private fun LanguagePickerDialogPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LanguagePickerDialogContent(
                modifier = Modifier,
                currentLanguage = AppLanguage.VIETNAMESE,
                onConfirm = {},
                onDismiss = {},
            )
        }
    }
}

// endregion
