package com.doannd3.treetask.core.datastore

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class TokenManager
@Inject
constructor(
    @ApplicationContext private val context: Context,
) {
    // Luồng báo hiệu Token đã chết, UI phải đá văng User ra ngoài màn hình Login
    private val _sessionExpiredEvent = MutableSharedFlow<Unit>()
    val sessionExpiredEvent = _sessionExpiredEvent.asSharedFlow()

    fun getAccessToken(): Flow<String?> =
        context.authDataStore.data.map { prefs ->
            prefs[KEY_ACCESS_TOKEN]
        }

    fun getRefreshToken(): Flow<String?> =
        context.authDataStore.data.map { prefs ->
            prefs[KEY_REFRESH_TOKEN]
        }

    suspend fun saveToken(
        accessToken: String,
        refreshToken: String,
    ) {
        context.authDataStore.edit { prefs ->
            prefs[KEY_ACCESS_TOKEN] = accessToken
            prefs[KEY_REFRESH_TOKEN] = refreshToken
        }
    }

    suspend fun clearToken() {
        context.authDataStore.edit { prefs ->
            prefs.remove(KEY_ACCESS_TOKEN)
            prefs.remove(KEY_REFRESH_TOKEN)
        }
        // bắn event chết session => navigate to login
        _sessionExpiredEvent.emit(Unit)
    }

    companion object {
        private val KEY_ACCESS_TOKEN = stringPreferencesKey("ACCESS_TOKEN")
        private val KEY_REFRESH_TOKEN = stringPreferencesKey("REFRESH_TOKEN")
    }
}
