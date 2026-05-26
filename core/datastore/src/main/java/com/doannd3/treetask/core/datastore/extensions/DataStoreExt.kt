package com.doannd3.treetask.core.datastore.extensions

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// ─── DataStore instances (internal to datastore module) ──────────────────────

internal val Context.userDataStore by preferencesDataStore("user_prefs")

internal val Context.deviceDataStore by preferencesDataStore("device_prefs")

internal val Context.appLanguageDataStore by preferencesDataStore("app_language_prefs")

internal val Context.darkModeDataStore by preferencesDataStore("dark_mode_prefs")
