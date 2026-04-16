package com.doannd3.treetask.feature.auth.ui.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.feature.auth.ui.login.EmailInput
import com.doannd3.treetask.feature.auth.ui.login.PasswordInput

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
        onEvent = viewModel::onEvent,
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

@Composable
fun RegisterScreen(
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
    onRegisterBack: () -> Unit
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing
    ) { paddingValues ->
        RegisterContent(
            modifier = Modifier.padding(bottom = paddingValues.calculateBottomPadding()),
            state = state,
            onEvent = onEvent,
            onRegisterBack = onRegisterBack,
        )
    }
}

@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
    onRegisterBack: () -> Unit
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding()
    ) {
        RegisterHeader(onRegisterBack = onRegisterBack)

        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            FullNameInput(
                fullName = state.fullName,
                onFullNameChange = { onEvent(RegisterEvent.FullNameChanged(it)) }
            )

            Spacer(modifier = Modifier.height(16.dp))

            EmailInput(
                email = state.email,
                onEmailChange = { onEvent(RegisterEvent.EmailChanged(it)) }
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordInput(
                password = state.password,
                passwordVisible = state.passwordVisible,
                onPasswordChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
                onPasswordVisibleChange = { onEvent(RegisterEvent.PasswordVisibleChanged(it)) },
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegisterButton(
                onSubmitRegister = { onEvent(RegisterEvent.SubmitRegister) }
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun RegisterScreenPreview() {
    RegisterScreen(
        state = RegisterState(
            fullName = "Nguyễn Demo",
            email = "demo@gmail.com",
            password = "123456"
        ),
        onEvent = {},
        onRegisterBack = {}
    )
}