package com.doannd3.treetask.core.network.model.request

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
data class RegisterDeviceTokenRequest(
    @SerialName("token") val token: String,
    @SerialName("platform") val platform: String = "android",
)

@Serializable
data class UnRegisterTokenRequest(
    @SerialName("token") val token: String,
)
