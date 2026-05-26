package com.doannd3.treetask.feature.profile.ui.profile

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.domain.usecase.auth.LogoutUseCase
import com.doannd3.treetask.core.domain.usecase.setting.ObserveAppLanguageUseCase
import com.doannd3.treetask.core.domain.usecase.user.ObserveCurrentUserUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
@Inject
constructor(
    private val logoutUseCase: LogoutUseCase,
    private val observeCurrentUserUseCase: ObserveCurrentUserUseCase,
    private val observeAppLanguageUseCase: ObserveAppLanguageUseCase,
) : BaseViewModel(),
    MviViewModel<ProfileState, ProfileEvent, ProfileEffect> {
    private val _uiState = MutableStateFlow(ProfileState())
    override val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<ProfileEffect>()
    override val effect: SharedFlow<ProfileEffect> = _effect.asSharedFlow()

    init {
        executeSafe {
            observeCurrentUserUseCase().collect { user ->
                _uiState.update { it.copy(user = user) }
            }
        }

        executeSafe {
            observeAppLanguageUseCase().collect { appLanguage ->
                _uiState.update { it.copy(selectedLanguage = appLanguage) }
            }
        }
    }

    override fun onEvent(event: ProfileEvent) {
        when (event) {
            ProfileEvent.SubmitLogout -> {
                submitLogout()
            }

            ProfileEvent.OpenLanguagePicker -> {
                _uiState.update { it.copy(showLanguagePicker = true) }
            }

            ProfileEvent.DismissLanguagePicker -> {
                _uiState.update { it.copy(showLanguagePicker = false) }
            }

            is ProfileEvent.ConfirmLanguage -> {
                _uiState.update {
                    it.copy(
                        showLanguagePicker = false,
                        selectedLanguage = event.language,
                    )
                }

                viewModelScope.launch {
                    _effect.emit(ProfileEffect.ApplyLanguage(event.language))
                }
            }

            ProfileEvent.NavigateChangePassword -> {
                viewModelScope.launch {
                    _effect.emit(ProfileEffect.NavigateToChangePassword)
                }
            }
            ProfileEvent.NavigateEditProfile -> {
                viewModelScope.launch {
                    _effect.emit(ProfileEffect.NavigateToEditProfile)
                }
            }
            is ProfileEvent.ToggleDarkMode -> {
                _uiState.update { it.copy(isDarkMode = event.isDarkMode) }
            }
        }
    }

    private fun submitLogout() {
        val state = _uiState.value
        if (state.isLoading) {
            return
        }

        executeSafe {
            _uiState.update { it.copy(isLoading = true) }
            logoutUseCase()
            _uiState.update { it.copy(isLoading = false) }
            _effect.emit(ProfileEffect.NavigateToLogin)
        }
    }

    override fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}
