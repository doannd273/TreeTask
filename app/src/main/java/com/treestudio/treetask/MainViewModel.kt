package com.treestudio.treetask

import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.network.NetworkMonitor
import com.doannd3.treetask.core.datastore.token.TokenStorage
import com.doannd3.treetask.core.domain.repository.AuthRepository
import com.doannd3.treetask.core.domain.repository.UserRepository
import com.doannd3.treetask.core.domain.usecase.setting.ObserveAppLanguageUseCase
import com.doannd3.treetask.core.domain.usecase.setting.ObserveDarkModeUseCase
import com.doannd3.treetask.feature.auth.navigation.AuthGraphDestination
import com.doannd3.treetask.feature.tasks.navigation.TasksGraphDestination
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.flow.stateIn
import javax.inject.Inject

@HiltViewModel
class MainViewModel
@Inject
constructor(
    private val tokenStorage: TokenStorage,
    private val userRepository: UserRepository,
    private val authRepository: AuthRepository,
    private val observeAppLanguageUseCase: ObserveAppLanguageUseCase,
    private val observeDarkModeUseCase: ObserveDarkModeUseCase,
    networkMonitor: NetworkMonitor,
) : BaseViewModel() {
    var isLoadingMain by mutableStateOf(true)
        private set

    var appLanguageTag by mutableStateOf<String?>(null)
        private set

    var isDarkMode by mutableStateOf(false)
        private set

    var startDestination by mutableStateOf<Any?>(null)
        private set

    val isOnline: StateFlow<Boolean> = networkMonitor.isOnline
        .stateIn(
            scope = viewModelScope,
            started = SharingStarted.WhileSubscribed(5_000),
            initialValue = true,
        )

    init {
        executeSafe {
            observeAppLanguageUseCase().collect { appLanguage ->
                appLanguageTag = appLanguage.localeTag
            }
        }

        executeSafe {
            observeDarkModeUseCase().collect { enabled ->
                this@MainViewModel.isDarkMode = enabled
            }
        }

        executeSafe {
            val token = tokenStorage.getAccessToken().first()
            if (token.isNullOrEmpty()) {
                startDestination = AuthGraphDestination
                isLoadingMain = false
                return@executeSafe
            }

            val result = userRepository.getProfile()
            when (result) {
                is ApiResult.Success -> {
                    startDestination = TasksGraphDestination
                    isLoadingMain = false
                }

                is ApiResult.Error -> {
                    val cached = userRepository.getCachedProfile().first()
                    startDestination =
                        if (cached != null) {
                            TasksGraphDestination
                        } else {
                            AuthGraphDestination
                        }
                    isLoadingMain = false
                }
            }
        }

        authRepository.isSessionExpired.onEach {
            startDestination = AuthGraphDestination
        }.launchSafeIn(viewModelScope)
    }
}
