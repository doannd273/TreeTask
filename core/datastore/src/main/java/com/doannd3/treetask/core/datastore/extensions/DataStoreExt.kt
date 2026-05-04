package com.doannd3.treetask.core.datastore.extensions

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

// ─── DataStore instances (internal to datastore module) ──────────────────────

internal val Context.authDataStore by preferencesDataStore("auth_prefs")

internal val Context.userDataStore by preferencesDataStore("user_prefs")

internal val Context.deviceDataStore by preferencesDataStore("device_prefs")
