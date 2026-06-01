package com.doannd3.treetask.core.domain.usecase.setting

import com.doannd3.treetask.core.domain.repository.SettingRepository
import com.doannd3.treetask.core.model.profile.AppLanguage
import javax.inject.Inject

class SetAppLanguageUseCase
    @Inject
    constructor(
        private val settingRepository: SettingRepository,
    ) {
        suspend operator fun invoke(appLanguage: AppLanguage) {
            settingRepository.saveAppLanguage(appLanguage)
        }
    }
