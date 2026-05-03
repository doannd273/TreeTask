package com.doannd3.treetask.feature.auth.ui.login

import com.doannd3.treetask.core.common.ApiResult
import com.doannd3.treetask.core.common.BaseViewModel
import com.doannd3.treetask.core.common.MviViewModel
import com.doannd3.treetask.core.common.R
import com.doannd3.treetask.core.common.UiText
import com.doannd3.treetask.core.domain.usecase.auth.LoginUseCase
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
class LoginViewModel @Inject constructor(
    private val loginUseCase: LoginUseCase,
) : BaseViewModel(), MviViewModel<LoginState, LoginEvent, LoginEffect> {

    private val _uiState = MutableStateFlow(LoginState())
    override val uiState: StateFlow<LoginState> = _uiState.asStateFlow()

    private val _effect = MutableSharedFlow<LoginEffect>()
    override val effect: SharedFlow<LoginEffect> = _effect.asSharedFlow()

    override fun onEvent(event: LoginEvent) {
        when (event) {
            is LoginEvent.EmailChanged -> {
                _uiState.update { it.copy(email = event.email) }
            }
            is LoginEvent.PasswordChanged -> {
                _uiState.update { it.copy(password = event.password) }
            }
            is LoginEvent.SubmitLogin -> submitLogin()
            is LoginEvent.PasswordVisibleChanged -> {
                _uiState.update { it.copy(passwordVisible = event.isVisible) }
            }
        }
    }

    private fun submitLogin() {
        val state = uiState.value

        executeSafe {
            _uiState.update { it.copy(isLoading = true) }

            val result = loginUseCase(state.email, state.password)

            _uiState.update { it.copy(isLoading = false) }

            when (result) {
                is ApiResult.Success -> {
                    _effect.emit(LoginEffect.NavigateToHome)
                }
                is ApiResult.Error -> {
                    val message = result.message ?: UiText.StringResource(R.string.common_error_unknown)
                    _effect.emit(LoginEffect.ShowErrorMessage(message))
                }
            }
        }
    }

    override fun setLoading(isLoading: Boolean) {
        _uiState.update { it.copy(isLoading = isLoading) }
    }
}
