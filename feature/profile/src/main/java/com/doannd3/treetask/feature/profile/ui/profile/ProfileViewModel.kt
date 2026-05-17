package com.doannd3.treetask.feature.profile.ui.profile

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.usecase.auth.LogoutUseCase
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asSharedFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import javax.inject.Inject

@HiltViewModel
class ProfileViewModel
    @Inject
    constructor(
        private val logoutUseCase: LogoutUseCase,
    ) : BaseViewModel(),
        MviViewModel<ProfileState, ProfileEvent, ProfileEffect> {
        private val _uiState = MutableStateFlow(ProfileState())
        override val uiState: StateFlow<ProfileState> = _uiState.asStateFlow()

        private val _effect = MutableSharedFlow<ProfileEffect>()
        override val effect: SharedFlow<ProfileEffect> = _effect.asSharedFlow()

        override fun onEvent(event: ProfileEvent) {
            when (event) {
                ProfileEvent.SubmitLogout -> {
                    submitLogout()
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
