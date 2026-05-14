package com.doannd3.treetask.feature.auth.ui.forgotpassword

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.usecase.auth.ForgotPasswordUseCase
import com.doannd3.treetask.feature.auth.R
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
class ForgotPasswordViewModel
    @Inject
    constructor(
        private val forgotPasswordUseCase: ForgotPasswordUseCase,
    ) : BaseViewModel(),
        MviViewModel<ForgotPasswordState, ForgotPasswordEvent, ForgotPasswordEffect> {
        override fun setLoading(isLoading: Boolean) {
            _uiState.update { it.copy(isLoading = isLoading) }
        }

        private val _uiState = MutableStateFlow(ForgotPasswordState())
        override val uiState: StateFlow<ForgotPasswordState> = _uiState.asStateFlow()

        private val _effect = MutableSharedFlow<ForgotPasswordEffect>()
        override val effect: SharedFlow<ForgotPasswordEffect> = _effect.asSharedFlow()

        override fun onEvent(event: ForgotPasswordEvent) {
            when (event) {
                is ForgotPasswordEvent.EmailChanged -> {
                    _uiState.update { it.copy(email = event.email) }
                }

                is ForgotPasswordEvent.SubmitForgotPassword -> {
                    submitForgotPassword()
                }
            }
        }

        private fun submitForgotPassword() {
            val state = uiState.value

            if (state.isLoading) {
                return
            }

            executeSafe {
                _uiState.update { it.copy(isLoading = true) }

                val result = forgotPasswordUseCase(state.email)

                _uiState.update { it.copy(isLoading = false) }

                when (result) {
                    is ApiResult.Success -> {
                        _effect.emit(
                            ForgotPasswordEffect.ShowSuccessMessage(
                                message = UiText.StringResource(R.string.auth_forgot_password_success),
                            ),
                        )
                    }

                    is ApiResult.Error -> {
                        val message = result.message ?: UiText.DynamicString("Lỗi không xác định")
                        _effect.emit(ForgotPasswordEffect.ShowErrorMessage(message))
                    }
                }
            }
        }
    }
