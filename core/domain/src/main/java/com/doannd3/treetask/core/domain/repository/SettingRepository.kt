package com.doannd3.treetask.core.domain.repository

import com.doannd3.treetask.core.model.profile.AppLanguage
import kotlinx.coroutines.flow.Flow

interface SettingRepository {
    fun getCachedAppLanguage(): Flow<AppLanguage>

    suspend fun saveAppLanguage(appLanguage: AppLanguage)
}
