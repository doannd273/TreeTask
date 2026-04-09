package com.doannd3.treetask.feature.auth.ui.forgotpassword

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
fun ForgotPasswordRoute(
    viewModel: ForgotPasswordViewModel = hiltViewModel(),
    onNavigateToLogin: () -> Unit,
    onForgotPasswordBack: () -> Unit
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    ForgotPasswordScreen(
        state = state,
        onEmailChange = { viewModel.onEvent(ForgotPasswordEvent.EmailChanged(it)) },
        onSubmitForgotPassword = { viewModel.onEvent(ForgotPasswordEvent.SubmitForgotPassword) },
        onForgotPasswordBack = onForgotPasswordBack
    )

    LaunchedEffect(viewModel.effect, lifecycleOwner) {
        lifecycleOwner.lifecycle.repeatOnLifecycle(Lifecycle.State.STARTED) {
            viewModel.effect.collect { effect ->
                when (effect) {

                    is ForgotPasswordEffect.ShowErrorMessage -> {
                        val errorStr = effect.message.asString(context)
                        globalAppState.showError(errorStr)
                    }

                    is ForgotPasswordEffect.NavigateToLogin -> {
                        onNavigateToLogin()
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