package com.doannd3.treetask.feature.auth.viewmodel

import androidx.lifecycle.viewModelScope
import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.usecase.auth.LoginUseCase
import com.doannd3.treetask.core.domain.usecase.auth.RegisterUseCase
import com.doannd3.treetask.feature.auth.contract.AuthEffect
import com.doannd3.treetask.feature.auth.contract.AuthEvent
import com.doannd3.treetask.feature.auth.contract.AuthState
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
class AuthViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
    private val registerUseCase: RegisterUseCase
) : BaseViewModel(), MviViewModel<AuthState, AuthEvent, AuthEffect> {

    private val _uiState = MutableStateFlow(AuthState())
    override val uiState: StateFlow<AuthState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<AuthEffect>()
    override val effect: SharedFlow<AuthEffect> = _effect.asSharedFlow()

    override fun onEvent(event: AuthEvent) {
        when (event) {
            is AuthEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email, emailError = null) }
            }
            is AuthEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password, passwordError = null) }
            }
            is AuthEvent.SubmitLogin -> submitLogin()
            is AuthEvent.SubmitRegister -> submitRegister()
        }
    }

    private fun submitLogin() {
        val state = uiState.value

        executeSafe {
            _uiState.update { it.copy(isLoading = true, emailError = null, passwordError = null) }

            val result = loginUseCase(state.email, state.password)

            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is ApiResult.Success -> {
                    _effect.emit(AuthEffect.NavigateToHome)
                }
                is ApiResult.Error -> {
                    val message = result.message ?: UiText.StringResource(R.string.common_error_unknown)
                    _effect.emit(AuthEffect.ShowErrorMessage(message))
                }
                is ApiResult.Loading -> Unit // Should not happen here since we handle loading via state
            }
        }
    }

    private fun submitRegister() {
        val state = uiState.value

        executeSafe {
            _uiState.update { it.copy(isLoading = true, emailError = null, passwordError = null) }

            val result = registerUseCase(state.email, state.password)

            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is ApiResult.Success -> {
                    _effect.emit(AuthEffect.NavigateToHome)
                }
                is ApiResult.Error -> {
                    val message = result.message ?: UiText.StringResource(R.string.common_error_unknown)
                    _effect.emit(AuthEffect.ShowErrorMessage(message))
                }
                is ApiResult.Loading -> Unit // Should not happen here since we handle loading via state
            }
        }
    }

    override fun handleUnexpectedError(throwable: Throwable) {
        _uiState.update { it.copy(isLoading = false) }
        val crashMsg = UiText.StringResource(R.string.common_error_unknown)
        viewModelScope.launch {
            _effect.emit(AuthEffect.ShowErrorMessage(crashMsg))
        }
    }
}