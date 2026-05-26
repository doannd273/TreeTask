package com.doannd3.treetask.core.datastore.language

import com.doannd3.treetask.core.model.profile.AppLanguage
import kotlinx.coroutines.flow.Flow

interface AppLanguageStorage {
    fun getAppLanguage(): Flow<AppLanguage>

    suspend fun saveAppLanguage(appLanguage: AppLanguage)

    suspend fun clearAppLanguage()
}
