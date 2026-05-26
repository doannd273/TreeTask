package com.doannd3.treetask.core.datastore.darkmode

import kotlinx.coroutines.flow.Flow

interface DarkModeStorage {
    suspend fun clearDarkMode()

    suspend fun saveDarkMode(isDarkMode: Boolean)

    fun getDarkMode(): Flow<Boolean>
}
