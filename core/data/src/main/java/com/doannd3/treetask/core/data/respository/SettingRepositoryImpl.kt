package com.doannd3.treetask.core.data.respository

import com.doannd3.treetask.core.datastore.darkmode.DarkModeStorage
import com.doannd3.treetask.core.datastore.language.AppLanguageStorage
import com.doannd3.treetask.core.domain.repository.SettingRepository
import com.doannd3.treetask.core.model.profile.AppLanguage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class SettingRepositoryImpl @Inject constructor(
    private val appLanguageStorage: AppLanguageStorage,
    private val darkModeStorage: DarkModeStorage,
) : SettingRepository {
    override fun getCachedAppLanguage(): Flow<AppLanguage> = appLanguageStorage.getAppLanguage()

    override suspend fun saveAppLanguage(appLanguage: AppLanguage) {
        appLanguageStorage.saveAppLanguage(appLanguage)
    }

    override fun getCachedDarkMode(): Flow<Boolean> {
        return darkModeStorage.getDarkMode()
    }

    override suspend fun saveDarkMode(isDarkMode: Boolean) {
        darkModeStorage.saveDarkMode(isDarkMode)
    }
}
