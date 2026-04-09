package com.doannd3.treetask.feature.auth.ui.register

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState

@Composable
fun RegisterRoute(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onRegisterBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    RegisterScreen(
        state = state,
        onFullNameChange = { viewModel.onEvent(RegisterEvent.FullNameChanged(it)) },
        onEmailChange = { viewModel.onEvent(RegisterEvent.EmailChanged(it)) },
        onPasswordChange = { viewModel.onEvent(RegisterEvent.PasswordChanged(it)) },
        onPasswordVisibleChange = { viewModel.onEvent(RegisterEvent.OnPasswordVisibleChanged(it)) },
        onSubmitRegister = { viewModel.onEvent(RegisterEvent.SubmitRegister) },
        onRegisterBack = onRegisterBack
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is RegisterEffect.NavigateToHome -> {
                        onNavigateToHome()
                    }

                    is RegisterEffect.ShowErrorMessage -> {
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
        if (state.isLoading) {
            globalAppState.showLoading()
        } else {
            globalAppState.hideLoading()
        }
    }
}