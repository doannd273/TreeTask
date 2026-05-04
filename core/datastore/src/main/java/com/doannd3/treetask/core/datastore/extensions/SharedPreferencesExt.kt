package com.doannd3.treetask.core.datastore.extensions

import android.content.SharedPreferences
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow

internal fun SharedPreferences.observeKey(key: String): Flow<String?> =
    callbackFlow {
        trySend(getString(key, null))

        val listener =
            SharedPreferences.OnSharedPreferenceChangeListener { _, changedKey ->
                if (changedKey == key) trySend(getString(key, null))
            }

        registerOnSharedPreferenceChangeListener(listener)
        awaitClose { unregisterOnSharedPreferenceChangeListener(listener) }
    }
