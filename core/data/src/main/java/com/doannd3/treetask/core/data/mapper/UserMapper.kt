package com.doannd3.treetask.core.data.mapper

import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.core.network.model.response.UserResponse

fun UserResponse.toUserOrNull(): User? {
    val id = id.requiredStringOrNull() ?: return null
    val fullName = fullName.requiredStringOrNull() ?: return null
    val email = email.requiredStringOrNull() ?: return null

    return User(
        id = id,
        fullName = fullName,
        email = email,
        avatar = avatar,
        phone = phone,
    )
}
