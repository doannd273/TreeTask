package com.doannd3.treetask.feature.auth.ui.register

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.usecase.auth.RegisterUseCase
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
class RegisterViewModel @Inject constructor(
    private val registerUseCase: RegisterUseCase
): BaseViewModel(), MviViewModel<RegisterState, RegisterEvent, RegisterEffect> {

    private val _uiState = MutableStateFlow(RegisterState())
    override val uiState: StateFlow<RegisterState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<RegisterEffect>()
    override val effect: SharedFlow<RegisterEffect> = _effect.asSharedFlow()

    override fun onEvent(event: RegisterEvent) {
        when (event) {
            is RegisterEvent.FullNameChanged -> {
                _uiState.update { it.copy(fullName = event.fullName) }
            }
            is RegisterEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }
            is RegisterEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }
            is RegisterEvent.SubmitRegister -> submitRegister()
            is RegisterEvent.OnPasswordVisibleChanged -> {
                _uiState.update { it.copy(passwordVisible = event.passwordVisible) }
            }
        }
    }

    private fun submitRegister() {
        val state = uiState.value

        executeSafe {
            _uiState.update { it.copy(isLoading = true) }

            val result = registerUseCase(state.fullName, state.email, state.password)

            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is ApiResult.Success -> {
                    _effect.emit(RegisterEffect.NavigateToHome)
                }
                is ApiResult.Error -> {
                    val message = result.message ?: UiText.StringResource(R.string.common_error_unknown)
                    _effect.emit(RegisterEffect.ShowErrorMessage(message))
                }
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}