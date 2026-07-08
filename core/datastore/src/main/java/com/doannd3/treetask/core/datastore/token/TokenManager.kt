package com.doannd3.treetask.core.datastore.token

import android.content.SharedPreferences
import androidx.core.content.edit
import com.doannd3.treetask.core.datastore.di.EncryptedPrefs
import com.doannd3.treetask.core.datastore.extensions.observeKey
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import javax.inject.Inject
import javax.inject.Singleton

/**
 * Implementation của [TokenStorage] sử dụng EncryptedSharedPreferences.
 *
 * EncryptedSharedPreferences mã hóa cả KEY lẫn VALUE bằng AES-256-GCM (nội dung)
 * và AES-256-SIV (key), được bảo vệ bởi Android Keystore — không thể đọc nếu
 * không có khóa riêng của thiết bị.
 *
 * KHÔNG dùng DataStore thường để lưu token vì file .preferences_pb không được mã hóa.
 */
@Singleton
class TokenManager
@Inject
constructor(
    @EncryptedPrefs private val encryptedPrefs: SharedPreferences,
) : TokenStorage {
    private val _sessionExpiredEvent = MutableSharedFlow<Unit>()
    override val sessionExpiredEvent: SharedFlow<Unit> = _sessionExpiredEvent.asSharedFlow()

    override fun getAccessToken(): Flow<String?> = encryptedPrefs.observeKey(KEY_ACCESS_TOKEN)

    override fun getRefreshToken(): Flow<String?> = encryptedPrefs.observeKey(KEY_REFRESH_TOKEN)

    override suspend fun saveToken(
        accessToken: String,
        refreshToken: String,
    ) {
        encryptedPrefs.edit {
            putString(KEY_ACCESS_TOKEN, accessToken)
            putString(KEY_REFRESH_TOKEN, refreshToken)
        }
    }

    override suspend fun clearToken() {
        encryptedPrefs.edit {
            remove(KEY_ACCESS_TOKEN)
            remove(KEY_REFRESH_TOKEN)
        }
        _sessionExpiredEvent.emit(Unit)
    }

    companion object {
        private const val KEY_ACCESS_TOKEN = "ACCESS_TOKEN"
        private const val KEY_REFRESH_TOKEN = "REFRESH_TOKEN"
    }
}
