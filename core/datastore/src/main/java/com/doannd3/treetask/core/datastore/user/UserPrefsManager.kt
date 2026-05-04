package com.doannd3.treetask.core.datastore.user

import android.content.Context
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.doannd3.treetask.core.datastore.extensions.userDataStore
import com.doannd3.treetask.core.model.user.User
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class UserPrefsManager
@Inject
constructor(
    @ApplicationContext private val context: Context,
) : UserStorage {
    override suspend fun saveUserProfile(user: User) {
        context.userDataStore.edit { prefs ->
            prefs[KEY_USER_ID] = user.id
            prefs[KEY_FULL_NAME] = user.fullName
            prefs[KEY_EMAIL] = user.email

            user.avatar?.let {
                prefs[KEY_AVATAR] = it
            } ?: run {
                prefs.remove(KEY_AVATAR)
            }

            user.phone?.let {
                prefs[KEY_PHONE] = it
            } ?: run {
                prefs.remove(KEY_PHONE)
            }
        }
    }

    override fun getUserProfile(): Flow<User?> =
        context.userDataStore.data.map { prefs ->
            val userId = prefs[KEY_USER_ID] ?: return@map null
            val email = prefs[KEY_EMAIL] ?: return@map null

            User(
                id = userId,
                fullName = prefs[KEY_FULL_NAME] ?: "",
                email = email,
                avatar = prefs[KEY_AVATAR],
                phone = prefs[KEY_PHONE],
            )
        }

    override suspend fun clearUserProfile() {
        context.userDataStore.edit { it.clear() }
    }

    companion object {
        private val KEY_USER_ID = stringPreferencesKey("USER_ID")
        private val KEY_FULL_NAME = stringPreferencesKey("USER_FULL_NAME")
        private val KEY_EMAIL = stringPreferencesKey("USER_EMAIL")
        private val KEY_AVATAR = stringPreferencesKey("USER_AVATAR")
        private val KEY_PHONE = stringPreferencesKey("USER_PHONE")
    }
}
