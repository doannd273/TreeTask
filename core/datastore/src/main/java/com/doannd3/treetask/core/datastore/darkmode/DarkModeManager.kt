package com.doannd3.treetask.core.datastore.darkmode

import android.content.Context
import androidx.datastore.preferences.core.booleanPreferencesKey
import androidx.datastore.preferences.core.edit
import com.doannd3.treetask.core.datastore.extensions.darkModeDataStore
import dagger.hilt.android.qualifiers.ApplicationContext
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import javax.inject.Inject

class DarkModeManager
    @Inject
    constructor(
        @ApplicationContext private val context: Context,
    ) : DarkModeStorage {
        override suspend fun clearDarkMode() {
            context.darkModeDataStore.edit {
                it.clear()
            }
        }

        override suspend fun saveDarkMode(isDarkMode: Boolean) {
            context.darkModeDataStore.edit { prefs ->
                prefs[IS_DARK_MODE] = isDarkMode
            }
        }

        override fun getDarkMode(): Flow<Boolean> {
            return context.darkModeDataStore.data.map { prefs -> prefs[IS_DARK_MODE] ?: false }
        }

        companion object {
            private val IS_DARK_MODE = booleanPreferencesKey("IS_DARK_MODE")
        }
    }
