package com.doannd3.treetask.feature.profile.ui.profile

import com.doannd3.treetask.core.model.user.User

data class ProfileState(
    val isLoading: Boolean = false,
    val user: User? = null
)

sealed class ProfileEvent {

}

sealed class ProfileEffect {

}
