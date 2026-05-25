package com.doannd3.treetask.feature.profile.ui.edit

import android.net.Uri
import com.doannd3.treetask.core.common.UiText

data class EditProfileState(
    val email: String = "",
    val fullName: String = "",
    val avatarUrl: String = "",
    val avatarUri: Uri? = null,
    val phone: String = "",
    val isLoading: Boolean = false,
)

sealed class EditProfileEvent {

    data class AvatarChanged(
        val avatarUri: Uri?,
    ) : EditProfileEvent()

    data class FullNameChanged(
        val fullName: String,
    ) : EditProfileEvent()

    data class PhoneChanged(
        val phone: String,
    ) : EditProfileEvent()

    data object SubmitEditProfile : EditProfileEvent()

    data object BackClicked : EditProfileEvent()

    data object SuccessAcknowledged : EditProfileEvent()
}

sealed class EditProfileEffect {
    data class ShowErrorMessage(
        val message: UiText,
    ) : EditProfileEffect()

    data class ShowSuccessMessage(
        val message: UiText,
    ) : EditProfileEffect()

    data object NavigateBack : EditProfileEffect()
}
