package com.doannd3.treetask.core.datastore

import android.content.Context
import androidx.datastore.preferences.preferencesDataStore

val Context.authDataStore by preferencesDataStore("auth_prefs")

val Context.userDataStore by preferencesDataStore("user_prefs")

val Context.deviceDataStore by preferencesDataStore("device_prefs")
