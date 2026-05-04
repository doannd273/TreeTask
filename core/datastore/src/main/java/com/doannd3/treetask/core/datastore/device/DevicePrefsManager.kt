package com.doannd3.treetask.core.datastore.device

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.doannd3.treetask.core.datastore.extensions.deviceDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevicePrefsManager
@Inject
constructor(
    @ApplicationContext private val context: Context,
) : DeviceStorage {
    private var cachedDeviceId: String? = null
    private val mutex = Mutex()

    override suspend fun getDeviceId(): String {
        cachedDeviceId?.let { return it }

        return mutex.withLock {
            cachedDeviceId?.let { return it }

            val id =
                context.deviceDataStore.data
                    .map { it[KEY_DEVICE_ID] }
                    .first()
            if (!id.isNullOrEmpty()) {
                cachedDeviceId = id
                return@withLock id
            }

            val newId = generateId()
            context.deviceDataStore.edit { it[KEY_DEVICE_ID] = newId }
            cachedDeviceId = newId
            newId
        }
    }

    @SuppressLint("HardwareIds")
    private fun generateId(): String {
        var deviceId =
            Settings.Secure.getString(
                context.contentResolver,
                Settings.Secure.ANDROID_ID,
            )
        if (deviceId.isNullOrEmpty() || deviceId.startsWith("00000")) {
            deviceId = "00000" + UUID.randomUUID().toString()
        }
        return deviceId
    }

    companion object {
        private val KEY_DEVICE_ID = stringPreferencesKey("device_id")
    }
}
