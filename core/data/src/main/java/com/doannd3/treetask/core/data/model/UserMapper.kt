package com.doannd3.treetask.core.data.model

import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.core.network.model.response.UserResponse

fun UserResponse.toUserOrNull(): User? {
    val id = this.id?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val fullName = this.fullName?.trim()?.takeIf { it.isNotEmpty() } ?: return null
    val email = email?.trim()?.takeIf { it.isNotEmpty() } ?: return null

    return User(
        id = id,
        fullName = fullName,
        email = email,
        avatar = avatar.orEmpty(),
        phone = phone.orEmpty(),
    )
}
