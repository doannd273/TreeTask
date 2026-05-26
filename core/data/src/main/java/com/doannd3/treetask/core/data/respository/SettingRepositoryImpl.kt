package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.datastore.language.AppLanguageStorage
import com.doannd3.treetask.core.domain.repository.SettingRepository
import com.doannd3.treetask.core.model.profile.AppLanguage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val appLanguageStorage: AppLanguageStorage,
) : SettingRepository {
    override fun getCachedAppLanguage(): Flow<AppLanguage> = appLanguageStorage.getAppLanguage()

    override suspend fun saveAppLanguage(appLanguage: AppLanguage) {
        appLanguageStorage.saveAppLanguage(appLanguage)
    }
}
