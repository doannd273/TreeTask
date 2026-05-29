package com.doannd3.treetask.core.domain.usecase.setting

import com.doannd3.treetask.core.domain.repository.SettingRepository
import javax.inject.Inject

class SaveDarkModeUseCase
    @Inject
    constructor(
        private val settingRepository: SettingRepository,
    ) {
        suspend operator fun invoke(isDarkMode: Boolean) {
            settingRepository.saveDarkMode(isDarkMode)
        }
    }
