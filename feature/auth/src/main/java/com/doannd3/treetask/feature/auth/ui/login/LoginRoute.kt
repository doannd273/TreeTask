package com.doannd3.treetask.feature.auth.ui.login

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import androidx.lifecycle.viewmodel.compose.viewModel
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState

@Composable
fun LoginRoute(
    viewModel: LoginViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit,
    onNavigateToForgotPassword: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    LoginScreen(
        state = state,
        onEmailChange = { viewModel.onEvent(LoginEvent.EmailChanged(it)) },
        onPasswordChange = { viewModel.onEvent(LoginEvent.PasswordChanged(it)) },
        onSubmitLogin = { viewModel.onEvent(LoginEvent.SubmitLogin) },
        onPasswordVisibleChange = { viewModel.onEvent(LoginEvent.PasswordVisibleChanged(it)) },
        onNavigateToRegister = onNavigateToRegister,
        onNavigateToForgotPassword = onNavigateToForgotPassword
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is LoginEffect.NavigateToHome -> {
                        onNavigateToHome()
                    }

                    is LoginEffect.ShowErrorMessage -> {
                        val errorStr = effect.message.asString(context)
                        globalAppState.showError(errorStr)
                    }
                }
            }
        }
    }

    // Lỗi crash/unexpected từ BaseViewModel (CoroutineExceptionHandler)
    LaunchedEffect(viewModel.baseErrorEffect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.baseErrorEffect.collect { message ->
                globalAppState.showError(message.asString(context))
            }
        }
    }

    LaunchedEffect(state.isLoading) {
        if (state.isLoading) globalAppState.showLoading()
        else globalAppState.hideLoading()
    }
}