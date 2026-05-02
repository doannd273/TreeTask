package com.doannd3.treetask.core.data.model

import com.doannd3.treetask.core.model.user.User
import com.doannd3.treetask.core.network.model.response.UserResponse

fun UserResponse.toUser(): User {
    return User(
        id = this.id.orEmpty(),
        fullName = this.fullName.orEmpty(),
        email = this.email.orEmpty(),
        avatar = this.avatar.orEmpty(),
        phone = this.phone.orEmpty(),
    )
}
