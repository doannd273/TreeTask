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
import com.doannd3.treetask.feature.auth.contract.AuthEffect
import com.doannd3.treetask.feature.auth.contract.AuthEvent
import com.doannd3.treetask.feature.auth.viewmodel.AuthViewModel

@Composable
fun RegisterRoute(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onRegisterBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    RegisterScreen(
        state = state,
        onFullNameChange = { viewModel.onEvent(AuthEvent.FullNameChanged(it)) },
        onEmailChange = { viewModel.onEvent(AuthEvent.EmailChanged(it)) },
        onPasswordChange = { viewModel.onEvent(AuthEvent.PasswordChanged(it)) },
        onSubmitRegister = { viewModel.onEvent(AuthEvent.SubmitRegister) },
        onRegisterBack = onRegisterBack
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {
                    is AuthEffect.NavigateToHome -> {
                        onNavigateToHome()
                    }

                    is AuthEffect.ShowErrorMessage -> {
                        val errorStr = effect.message.asString(context)
                        globalAppState.showError(errorStr)
                    }
                }
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