package com.doannd3.treetask.core.datastore

import android.annotation.SuppressLint
import android.content.Context
import android.provider.Settings
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.sync.Mutex
import kotlinx.coroutines.sync.withLock
import java.util.UUID
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class DevicePrefsManager @Inject constructor(
    @ApplicationContext private val context: Context
) {
    private var cachedDeviceId: String? = null
    private val mutex = Mutex()

    suspend fun getDeviceId(): String {
        // Trả về ngay nếu đã có trong RAM
        cachedDeviceId?.let { return it }

        return mutex.withLock {
            // Kiểm tra lại lần nữa sau khi có Lock
            cachedDeviceId?.let { return it }

            // Đọc từ file DataStore
            val id = context.deviceDataStore.data.map { it[KEY_DEVICE_ID] }.first()
            if (!id.isNullOrEmpty()) {
                cachedDeviceId = id
                return@withLock id
            }

            // Nếu hoàn toàn chưa có, tiến hành tạo mới
            val newId = generateId()

            // Lưu vào file
            context.deviceDataStore.edit { it[KEY_DEVICE_ID] = newId }

            // Cập nhật RAM cache
            cachedDeviceId = newId
            newId
        }
    }

    @SuppressLint("HardwareIds")
    private fun generateId(): String {
        // 1. Thử lấy ANDROID_ID từ hệ thống
        var deviceId = Settings.Secure.getString(
            context.contentResolver,
            Settings.Secure.ANDROID_ID
        )
        // 2. Kiểm tra nếu ANDROID_ID không hợp lệ (null hoặc toàn số 0)
        if (deviceId.isNullOrEmpty() || deviceId.startsWith("00000")) {
            // Tự tạo UUID và thêm tiền tố để dễ phân biệt
            deviceId = "00000" + UUID.randomUUID().toString()
        }
        return deviceId
    }

    companion object {
        private val KEY_DEVICE_ID = stringPreferencesKey("device_id")
    }
}