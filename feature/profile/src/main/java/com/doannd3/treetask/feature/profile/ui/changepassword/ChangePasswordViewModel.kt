package com.doannd3.treetask.feature.profile.ui.changepassword

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.common.toDisplayMessage
import com.doannd3.treetask.core.domain.usecase.user.ChangePasswordUseCase
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
import com.doannd3.treetask.core.common.R as CommonR
import com.doannd3.treetask.feature.profile.R as ProfileR

@HiltViewModel
class ChangePasswordViewModel
    @Inject
    constructor(
        private val changePasswordUseCase: ChangePasswordUseCase,
    ) : BaseViewModel(),
        MviViewModel<ChangePasswordState, ChangePasswordEvent, ChangePasswordEffect> {
        private val _uiState = MutableStateFlow(ChangePasswordState())
        override val uiState: StateFlow<ChangePasswordState> = _uiState.asStateFlow()

        private val _effect = MutableSharedFlow<ChangePasswordEffect>()
        override val effect: SharedFlow<ChangePasswordEffect> = _effect.asSharedFlow()

        override fun onEvent(event: ChangePasswordEvent) {
            when (event) {
                is ChangePasswordEvent.CurrentPasswordChanged -> {
                    _uiState.update { it.copy(currentPassword = event.currentPassword) }
                }

                is ChangePasswordEvent.NewPasswordChanged -> {
                    _uiState.update { it.copy(newPassword = event.newPassword) }
                }

                is ChangePasswordEvent.ConfirmPasswordChanged -> {
                    _uiState.update { it.copy(confirmPassword = event.confirmPassword) }
                }

                is ChangePasswordEvent.CurrentPasswordVisibleChanged -> {
                    _uiState.update { it.copy(currentPasswordVisible = event.visible) }
                }

                is ChangePasswordEvent.NewPasswordVisibleChanged -> {
                    _uiState.update { it.copy(newPasswordVisible = event.visible) }
                }

                is ChangePasswordEvent.ConfirmPasswordVisibleChanged -> {
                    _uiState.update { it.copy(confirmPasswordVisible = event.visible) }
                }

                ChangePasswordEvent.SuccessAcknowledged -> {
                    viewModelScope.launch {
                        _effect.emit(ChangePasswordEffect.NavigateBack)
                    }
                }

                ChangePasswordEvent.SubmitChangePassword -> {
                    submitChangePassword()
                }
            }
        }

        private fun submitChangePassword() {
            val state = _uiState.value
            if (state.isLoading) {
                return
            }

            executeSafe {
                if (state.newPassword != state.confirmPassword) {
                    _effect.emit(
                        ChangePasswordEffect.ShowErrorMessage(
                            UiText.StringResource(
                                ProfileR.string.profile_change_password_mismatch_error,
                            ),
                        ),
                    )
                    return@executeSafe
                }

                _uiState.update { it.copy(isLoading = true) }
                val result =
                    changePasswordUseCase(
                        currentPassword = state.currentPassword,
                        newPassword = state.newPassword,
                    )
                _uiState.update { it.copy(isLoading = false) }

                when (result) {
                    is ApiResult.Success -> {
                        val message =
                            result.message
                                ?: UiText.StringResource(ProfileR.string.profile_change_password_success)
                        _effect.emit(ChangePasswordEffect.ShowSuccessMessage(message))
                    }

                    is ApiResult.Error -> {
                        val message =
                            result.toDisplayMessage(
                                UiText.StringResource(CommonR.string.common_error_unknown),
                            )
                        _effect.emit(ChangePasswordEffect.ShowErrorMessage(message))
                    }
                }
            }
        }

        override fun setLoading(isLoading: Boolean) {
            _uiState.update { it.copy(isLoading = isLoading) }
        }
    }
