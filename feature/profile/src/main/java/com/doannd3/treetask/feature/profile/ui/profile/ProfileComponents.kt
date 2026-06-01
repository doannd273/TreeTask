package com.doannd3.treetask.feature.profile.ui.profile

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.selection.toggleable
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.KeyboardArrowRight
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.ModalBottomSheet
import androidx.compose.material3.Surface
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import com.doannd3.treetask.core.designsystem.component.CommonSearch
import com.doannd3.treetask.core.designsystem.theme.AppPreviewLightDark
import com.doannd3.treetask.core.designsystem.theme.TreeTaskTheme
import com.doannd3.treetask.core.model.profile.AppLanguage
import com.doannd3.treetask.feature.profile.R

// region LanguagePickerBottomSheet

@OptIn(ExperimentalMaterial3Api::class)
@Composable
internal fun LanguagePickerBottomSheet(
    currentLanguage: AppLanguage,
    onLanguageSelected: (AppLanguage) -> Unit,
    onDismiss: () -> Unit,
) {
    var searchQuery by rememberSaveable { mutableStateOf("") }

    val languagesDisplayNames =
        AppLanguage.entries.associateWith { language ->
            stringResource(language.displayNameResId())
        }

    val languagesNativeNames =
        AppLanguage.entries.associateWith { language ->
            stringResource(language.nativeNameResId())
        }

    val filteredLanguages =
        remember(searchQuery, languagesDisplayNames, languagesNativeNames) {
            val query = searchQuery.trim()
            if (query.isBlank()) {
                AppLanguage.entries
            } else {
                AppLanguage.entries.filter { language ->
                    languagesDisplayNames.getValue(language).contains(query, ignoreCase = true) ||
                        languagesNativeNames.getValue(language).contains(query, ignoreCase = true)
                }
            }
        }

    ModalBottomSheet(
        onDismissRequest = onDismiss,
    ) {
        Column(
            modifier = Modifier.fillMaxWidth().padding(16.dp),
        ) {
            Text(
                modifier = Modifier.fillMaxWidth(),
                text = stringResource(R.string.profile_dialog_language_title),
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(16.dp))

            CommonSearch(
                hintText = R.string.profile_language_search_hint,
                isLoadingSearch = false,
                searchQuery = searchQuery,
                onSearchChange = {
                    searchQuery = it
                },
                onClearClick = {
                    searchQuery = ""
                },
            )

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.profile_language_selected),
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(16.dp))

            LanguageItemRow(
                flagIcon = currentLanguage.flagIconRes(),
                displayName = currentLanguage.displayNameResId(),
                nativeName = currentLanguage.nativeNameResId(),
                showCheckmark = true,
            )

            HorizontalDivider(thickness = 0.3.dp, color = MaterialTheme.colorScheme.outlineVariant)

            Spacer(Modifier.height(16.dp))

            Text(
                text = stringResource(R.string.profile_language_all),
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(8.dp))

            if (filteredLanguages.isEmpty()) {
                Text(
                    text = stringResource(R.string.profile_language_no_results),
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant,
                )
            }

            LazyColumn(
                modifier = Modifier.fillMaxWidth().weight(1f, fill = false),
                horizontalAlignment = Alignment.CenterHorizontally,
            ) {
                items(
                    items = filteredLanguages,
                    key = { appLanguage -> appLanguage.localeTag },
                ) { appLanguage ->
                    LanguageItemRow(
                        flagIcon = appLanguage.flagIconRes(),
                        displayName = appLanguage.displayNameResId(),
                        nativeName = appLanguage.nativeNameResId(),
                        showCheckmark = currentLanguage == appLanguage,
                    ) {
                        onLanguageSelected(appLanguage)
                    }
                }
            }
        }
    }
}

@AppPreviewLightDark
@Composable
private fun LanguagePickerBottomSheetPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LanguagePickerBottomSheet(
                currentLanguage = AppLanguage.VIETNAMESE,
                onLanguageSelected = {},
                onDismiss = {},
            )
        }
    }
}

// end region

// region LanguageItemRow

@Composable
internal fun LanguageItemRow(
    @DrawableRes flagIcon: Int,
    @StringRes displayName: Int,
    @StringRes nativeName: Int,
    showCheckmark: Boolean = false,
    onClick: (() -> Unit)? = null,
) {
    val languageDisplayName = stringResource(displayName)
    val languageNativeName = stringResource(nativeName)

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    },
                ).padding(
                    vertical = 8.dp,
                ),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Image(
            modifier = Modifier.size(22.dp),
            painter = painterResource(flagIcon),
            contentDescription = stringResource(R.string.profile_cd_language_flag, languageDisplayName),
        )

        Column(
            modifier = Modifier.weight(1f).padding(horizontal = 12.dp),
        ) {
            Text(
                text = languageNativeName,
                style = MaterialTheme.typography.bodyLarge,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )

            Spacer(Modifier.height(4.dp))

            Text(
                text = languageDisplayName,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            )
        }

        if (showCheckmark) {
            Icon(
                modifier = Modifier.size(22.dp),
                painter = painterResource(R.drawable.profile_ic_checkmark),
                contentDescription = stringResource(R.string.profile_cd_language_checkmark),
            )
        }
    }
}

@AppPreviewLightDark
@Composable
private fun LanguageItemRowPreview() {
    TreeTaskTheme {
        Surface(color = MaterialTheme.colorScheme.background) {
            LanguageItemRow(
                flagIcon = R.drawable.profile_ic_flag_vi,
                displayName = R.string.profile_language_vi,
                nativeName = R.string.profile_language_vi_native,
                showCheckmark = true,
            )
        }
    }
}

// endregion

// region ProfileHeader

@Composable
internal fun ProfileHeader(
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
            modifier =
                Modifier
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
internal fun ProfileSwitchItem(
    @DrawableRes iconRes: Int,
    title: String,
    checked: Boolean,
    onCheckedChange: (Boolean) -> Unit,
) {
    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .toggleable(
                    value = checked,
                    onValueChange = onCheckedChange,
                    role = Role.Switch,
                ).padding(horizontal = 16.dp, vertical = 24.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant,
        )
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
                    iconRes = R.drawable.profile_ic_dark_mode,
                    title = stringResource(R.string.profile_menu_dark_mode),
                    checked = true,
                    onCheckedChange = {},
                )
                HorizontalDivider()
                ProfileSwitchItem(
                    iconRes = R.drawable.profile_ic_dark_mode,
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
internal fun ProfileItem(
    @DrawableRes iconRes: Int,
    title: String,
    onClick: (() -> Unit)? = null,
    enabled: Boolean = true,
    trailing: @Composable (() -> Unit)? = null,
) {
    val contentAlpha = if (enabled) 1f else 0.38f

    Row(
        modifier =
            Modifier
                .fillMaxWidth()
                .then(
                    if (onClick != null && enabled) {
                        Modifier.clickable(onClick = onClick)
                    } else {
                        Modifier
                    },
                ).padding(horizontal = 16.dp, vertical = 14.dp),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        Icon(
            painter = painterResource(iconRes),
            contentDescription = null,
            modifier = Modifier.size(24.dp),
            tint = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = contentAlpha),
        )

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
                    iconRes = R.drawable.profile_ic_edit_profile,
                    title = stringResource(R.string.profile_menu_edit_profile),
                    onClick = {},
                )
                HorizontalDivider()
                ProfileItem(
                    iconRes = R.drawable.profile_ic_language,
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
internal fun ProfileSection(
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
            colors =
                CardDefaults.cardColors(
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
                    iconRes = R.drawable.profile_ic_edit_profile,
                    title = stringResource(R.string.profile_menu_edit_profile),
                    onClick = {},
                )
                HorizontalDivider()
                ProfileItem(
                    iconRes = R.drawable.profile_ic_change_password,
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
internal fun LogoutButton(
    isEnable: Boolean,
    onSubmitLogout: () -> Unit,
) {
    Button(
        onClick = onSubmitLogout,
        enabled = isEnable,
        modifier = Modifier.fillMaxWidth(),
        colors =
            ButtonDefaults.buttonColors(
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
            modifier =
                Modifier
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
