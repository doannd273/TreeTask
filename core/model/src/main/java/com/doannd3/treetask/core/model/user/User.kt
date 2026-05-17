package com.doannd3.treetask.core.model.user

data class User(
    val id: String,
    val email: String,
    val fullName: String,
    val avatar: String?,
    val phone: String?,
)
