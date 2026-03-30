package com.doannd3.treetask.feature.auth.ui

import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import androidx.hilt.navigation.compose.hiltViewModel
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import com.doannd3.treetask.feature.auth.contract.AuthEffect
import com.doannd3.treetask.feature.auth.viewmodel.AuthViewModel

@Composable
fun LoginScreen(
    viewModel: AuthViewModel = hiltViewModel(),
    onNavigateToHome: () -> Unit,
    onNavigateToRegister: () -> Unit
) {
    // Quan sát dữ liệu UI
    val state by viewModel.uiState.collectAsStateWithLifecycle()
    val context = LocalContext.current

    // Effect
    LaunchedEffect(viewModel.effect) {
        viewModel.effect.collect { effect ->
            when(effect) {
                is AuthEffect.NavigateToHome -> {
                    onNavigateToHome
                }

                is AuthEffect.ShowErrorMessage -> {

                }
            }
        }
    }

    // Giao diện chính
    Scaffold { paddingValues ->

    }

}

@Composable
@Preview(showBackground = true)
fun LoginScreenPreview() {

}