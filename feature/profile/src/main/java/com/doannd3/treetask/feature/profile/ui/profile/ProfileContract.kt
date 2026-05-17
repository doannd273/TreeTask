package com.doannd3.treetask.feature.profile.ui.profile

import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.model.user.User

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null,
)

sealed class ProfileEvent {
    data object SubmitLogout : ProfileEvent()
}

sealed class ProfileEffect {
    data object NavigateToLogin : ProfileEffect()

    data class ShowErrorMessage(
        val message: UiText,
    ) : ProfileEffect()
}
