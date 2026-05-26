package com.doannd3.treetask.core.domain.usecase.setting

import com.doannd3.treetask.core.domain.repository.SettingRepository
import com.doannd3.treetask.core.model.profile.AppLanguage
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveAppLanguageUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    operator fun invoke(): Flow<AppLanguage> {
        return settingRepository.getCachedAppLanguage()
    }
}
