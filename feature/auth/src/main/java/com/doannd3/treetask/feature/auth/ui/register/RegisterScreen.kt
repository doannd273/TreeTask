package com.doannd3.treetask.feature.auth.ui.register

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.focus.FocusRequester
import androidx.compose.ui.focus.focusRequester
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.text.input.ImeAction
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LocalLifecycleOwner
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import androidx.lifecycle.repeatOnLifecycle
import com.doannd3.treetask.core.common.asString
import com.doannd3.treetask.core.designsystem.component.LocalGlobalAppState
import com.doannd3.treetask.core.designsystem.util.rememberDebouncedClick
import com.doannd3.treetask.feature.auth.ui.login.EmailInput
import com.doannd3.treetask.feature.auth.ui.login.PasswordInput

@Composable
fun RegisterRoute(
    viewModel: RegisterViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onRegisterBack: () -> Unit,
) {
    val state by viewModel.uiState.collectAsStateWithLifecycle()

    val globalAppState = LocalGlobalAppState.current
    val context = LocalContext.current
    val lifecycleOwner = LocalLifecycleOwner.current

    RegisterScreen(
        state = state,
        onEvent = viewModel::onEvent,
        onRegisterBack = onRegisterBack,
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
    onRegisterBack: () -> Unit,
) {
    Scaffold(
        contentWindowInsets = WindowInsets.safeDrawing.only(
            WindowInsetsSides.Horizontal + WindowInsetsSides.Bottom,
        ),
        topBar = {
            RegisterHeader(onRegisterBack = onRegisterBack)
        },
    ) { paddingValues ->
        RegisterContent(
            modifier =
            Modifier.padding(
                paddingValues = paddingValues,
            ),
            state = state,
            onEvent = onEvent,
        )
    }
}

@Composable
fun RegisterContent(
    modifier: Modifier = Modifier,
    state: RegisterState,
    onEvent: (RegisterEvent) -> Unit,
) {
    val onSubmitRegisterDebounced =
        rememberDebouncedClick {
            onEvent(RegisterEvent.SubmitRegister)
        }

    val emailFocusRequester = remember { FocusRequester() }
    val passwordFocusRequester = remember { FocusRequester() }
    val focusManager = LocalFocusManager.current

    Column(
        modifier =
        modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
            .imePadding(),
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
        ) {
            FullNameInput(
                fullName = state.fullName,
                onFullNameChange = { onEvent(RegisterEvent.FullNameChanged(it)) },
                onImeNext = {
                    emailFocusRequester.requestFocus()
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            EmailInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(emailFocusRequester),
                email = state.email,
                onEmailChange = { onEvent(RegisterEvent.EmailChanged(it)) },
                imeAction = ImeAction.Next,
                onImeNext = {
                    passwordFocusRequester.requestFocus()
                },
            )

            Spacer(modifier = Modifier.height(8.dp))

            PasswordInput(
                modifier = Modifier
                    .fillMaxWidth()
                    .focusRequester(passwordFocusRequester),
                password = state.password,
                passwordVisible = state.passwordVisible,
                onPasswordChange = { onEvent(RegisterEvent.PasswordChanged(it)) },
                onPasswordVisibleChange = { onEvent(RegisterEvent.PasswordVisibleChanged(it)) },
                onImeDone = {
                    focusManager.clearFocus()
                    onSubmitRegisterDebounced()
                },
            )

            Spacer(modifier = Modifier.height(16.dp))

            RegisterButton(
                isEnable = !state.isLoading,
                onSubmitRegister = onSubmitRegisterDebounced,
            )
        }
    }
}

@Composable
@Preview(showBackground = true)
fun RegisterScreenPreview() {
    RegisterScreen(
        state =
        RegisterState(
            fullName = "Nguyễn Demo",
            email = "demo@gmail.com",
            password = "123456",
        ),
        onEvent = {},
        onRegisterBack = {},
    )
}
