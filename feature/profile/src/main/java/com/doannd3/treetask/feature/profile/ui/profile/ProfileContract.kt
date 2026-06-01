package com.doannd3.treetask.feature.profile.ui.profile

import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.profile.AppLanguage
import com.doannd3.treetask.core.model.user.User

data class ProfileState(
    val selectedLanguage: AppLanguage = AppLanguage.ENGLISH,
    val showLanguagePicker: Boolean = false,
    val isLoading: Boolean = false,
    val user: User? = null,
    val isDarkMode: Boolean = false,
)

sealed class ProfileEvent {
    data object SubmitLogout : ProfileEvent()

    data object OpenLanguagePicker : ProfileEvent()

    data object DismissLanguagePicker : ProfileEvent()

    data class SelectLanguage(
        val language: AppLanguage,
    ) : ProfileEvent()

    data object NavigateEditProfile : ProfileEvent()

    data object NavigateChangePassword : ProfileEvent()

    data class ToggleDarkMode(
        val isDarkMode: Boolean,
    ) : ProfileEvent()
}

sealed class ProfileEffect {
    data object NavigateToLogin : ProfileEffect()

    data class ShowErrorMessage(
        val message: UiText,
    ) : ProfileEffect()

    data object NavigateToChangePassword : ProfileEffect()

    data object NavigateToEditProfile : ProfileEffect()
}
