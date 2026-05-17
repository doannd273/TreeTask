package com.doannd3.treetask.core.network.model.response

import kotlinx.serialization.Serializable

@Serializable
data class ChangePasswordResponse(
    @Serializable val message: String,
)