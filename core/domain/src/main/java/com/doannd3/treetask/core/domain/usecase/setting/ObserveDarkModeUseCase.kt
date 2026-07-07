package com.doannd3.treetask.core.domain.usecase.setting

import com.doannd3.treetask.core.domain.repository.SettingRepository
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

class ObserveDarkModeUseCase @Inject constructor(
    private val settingRepository: SettingRepository,
) {
    operator fun invoke(): Flow<Boolean> {
        return settingRepository.getCachedDarkMode()
    }
}
