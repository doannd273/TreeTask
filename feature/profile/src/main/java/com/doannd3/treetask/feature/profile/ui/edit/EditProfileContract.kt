package com.doannd3.treetask.feature.profile.ui.edit

data class EditProfileState(
    val fullName: String = "",
    val email: String = "",
    val phone: String = "",
    val isLoading: Boolean = false,
)

sealed class EditProfileEvent {
    data class FullNameChanged(
        val fullName: String,
    ) : EditProfileEvent()

    data class EmailChanged(
        val email: String,
    ) : EditProfileEvent()

    data class PhoneChanged(
        val phone: String,
    ) : EditProfileEvent()

    data object SaveClicked : EditProfileEvent()

    data object BackClicked : EditProfileEvent()
}

sealed class EditProfileEffect {
    data object NavigateBack : EditProfileEffect()
}
