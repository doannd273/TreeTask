package com.doannd3.treetask.core.datastore.device

interface DeviceStorage {
    suspend fun getDeviceId(): String
}
